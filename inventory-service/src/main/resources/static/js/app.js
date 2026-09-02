/* =========================================================
   ESTADO GLOBAL
   ========================================================= */
const state = {
    token: null, username: null, rol: null,
    // Vacio = mismo origen. Si sirves este archivo desde Spring Boot (http://localhost:8080),
    // dejalo vacio y no necesitas configurar CORS para nada.
    apiBase: 'http://localhost:8080',
    bodegasCache: [], productosCache: []
};

/* =========================================================
   API WRAPPER
   ========================================================= */
class ApiError extends Error {
    constructor(status, message, data) {
        super(message);
        this.status = status;
        this.data = data;
    }
}

async function api(path, { method = 'GET', body, skipAuth = false } = {}) {
    const headers = { 'Content-Type': 'application/json' };
    if (state.token && !skipAuth) headers['Authorization'] = 'Bearer ' + state.token;

    let res;
    try {
        res = await fetch(state.apiBase + path, {
            method, headers,
            body: body !== undefined ? JSON.stringify(body) : undefined
        });
    } catch (networkErr) {
        throw new ApiError(0, 'No se pudo conectar con el servidor. Verifica que el backend este corriendo y la URL configurada.');
    }

    if (res.status === 204) return null;

    let data = null;
    const text = await res.text();
    if (text) {
        try { data = JSON.parse(text); } catch { data = null; }
    }

    if (!res.ok) {
        let message = 'Error en el servidor';
        if (data) {
            if (data.message) message = data.message;
            else if (data.errors) message = Object.values(data.errors).join(' · ');
        } else if (res.status === 403) {
            message = 'Sus permisos son insuficientes para realizar esta accion.';
        } else if (res.statusText) {
            message = res.statusText;
        }
        throw new ApiError(res.status, message, data);
    }
    return data;
}

/** Manejo centralizado de errores para acciones disparadas por el usuario. */
function handleApiError(err, containerId) {
    if (!(err instanceof ApiError)) {
        console.error(err);
        toast('Ocurrio un error inesperado.', 'error');
        return;
    }
    if (err.status === 401) {
        toast('Tu sesion expiro o es invalida. Inicia sesion de nuevo.', 'error');
        logout();
        return;
    }
    if (err.status === 403) {
        showDenied();
        return;
    }
    toast(err.message, 'error');
    if (containerId) renderError(containerId, err.message);
}

/* =========================================================
   HELPERS DE FORMATO / SEGURIDAD DE TEXTO
   ========================================================= */
function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str).replace(/[&<>"']/g, s => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[s]));
}

function formatDate(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    if (isNaN(d.getTime())) return iso;
    return d.toLocaleString('es-CO', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function formatMoney(n) {
    if (n === null || n === undefined) return '—';
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(n);
}

function toDateTimeSeconds(localValue) {
    // <input type="datetime-local"> entrega "yyyy-MM-ddTHH:mm" — el backend espera segundos.
    if (!localValue) return null;
    return localValue.length === 16 ? localValue + ':00' : localValue;
}

/* =========================================================
   TOASTS
   ========================================================= */
function toast(msg, type = 'info') {
    const stack = document.getElementById('toast-stack');
    const el = document.createElement('div');
    el.className = 'toast' + (type === 'error' ? ' error' : type === 'success' ? ' success' : '');
    el.textContent = msg;
    stack.appendChild(el);
    setTimeout(() => el.remove(), 3500);
}

/* =========================================================
   MODAL DE PERMISOS INSUFICIENTES (403)
   ========================================================= */
const deniedBackdrop = document.getElementById('denied-backdrop');
function showDenied() { deniedBackdrop.classList.add('active'); }
document.getElementById('denied-close').onclick = () => deniedBackdrop.classList.remove('active');
deniedBackdrop.addEventListener('click', (e) => { if (e.target === deniedBackdrop) deniedBackdrop.classList.remove('active'); });

/* =========================================================
   MODAL GENERICO (crear/editar/confirmar/detalle)
   ========================================================= */
const modalBackdrop = document.getElementById('modal-backdrop');
const modalEl = modalBackdrop.querySelector('.modal');
const modalTitle = document.getElementById('modal-title');
const modalBody = document.getElementById('modal-body');
const modalFooter = document.getElementById('modal-footer');

function openModal({ title, bodyHtml, footerHtml = '', wide = false }) {
    modalTitle.textContent = title;
    modalBody.innerHTML = bodyHtml;
    modalFooter.innerHTML = footerHtml;
    modalEl.classList.toggle('modal-wide', wide);
    modalBackdrop.classList.add('active');
}
function closeModal() {
    modalBackdrop.classList.remove('active');
    modalBody.innerHTML = '';
    modalFooter.innerHTML = '';
}
document.getElementById('modal-close').onclick = closeModal;
modalBackdrop.addEventListener('click', (e) => { if (e.target === modalBackdrop) closeModal(); });

function setBtnLoading(btn, loading, loadingText = 'Guardando...') {
    if (loading) {
        btn.dataset.originalText = btn.textContent;
        btn.disabled = true;
        btn.textContent = loadingText;
    } else {
        btn.disabled = false;
        btn.textContent = btn.dataset.originalText || btn.textContent;
    }
}

function confirmAction(message, onConfirm) {
    openModal({
        title: 'Confirmar accion',
        bodyHtml: `<p>${escapeHtml(message)}</p>`,
        footerHtml: `
            <button class="btn btn-ghost" id="confirm-cancel">Cancelar</button>
            <button class="btn btn-danger" id="confirm-ok">Si, continuar</button>
        `
    });
    document.getElementById('confirm-cancel').onclick = closeModal;
    document.getElementById('confirm-ok').onclick = async () => {
        const btn = document.getElementById('confirm-ok');
        setBtnLoading(btn, true, 'Procesando...');
        try {
            await onConfirm();
            closeModal();
        } catch (err) {
            setBtnLoading(btn, false);
            handleApiError(err);
        }
    };
}

/* =========================================================
   RENDER GENERICO DE TABLAS
   ========================================================= */
function renderLoading(containerId) {
    document.getElementById(containerId).innerHTML = '<div class="loading-state"><span class="loading-spinner"></span> Cargando...</div>';
}
function renderError(containerId, msg) {
    document.getElementById(containerId).innerHTML = `<div class="error-state">${escapeHtml(msg)}</div>`;
}
function renderTable(containerId, { columns, rows, actions, emptyMessage }) {
    const wrap = document.getElementById(containerId);
    if (!rows || rows.length === 0) {
        wrap.innerHTML = `<div class="empty-state">${escapeHtml(emptyMessage || 'No hay registros.')}</div>`;
        return;
    }
    let html = '<div class="table-scroll"><table class="data-table"><thead><tr>';
    columns.forEach(c => html += `<th>${c.label}</th>`);
    if (actions) html += '<th></th>';
    html += '</tr></thead><tbody>';
    rows.forEach(row => {
        html += '<tr>';
        columns.forEach(c => {
            const val = c.render ? c.render(row) : escapeHtml(row[c.key] ?? '—');
            html += `<td>${val}</td>`;
        });
        if (actions) html += `<td class="actions-cell">${actions(row)}</td>`;
        html += '</tr>';
    });
    html += '</tbody></table></div>';
    wrap.innerHTML = html;
}

/* =========================================================
   AUTH (LOGIN & REGISTER)
   ========================================================= */
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const authError = document.getElementById('auth-error');
const authTag = document.getElementById('auth-tag');

document.getElementById('btn-to-register').onclick = () => {
    loginForm.style.display = 'none';
    registerForm.style.display = 'block';
    authTag.textContent = 'Registro de nuevo empleado';
    authError.style.display = 'none';
};
document.getElementById('btn-to-login').onclick = () => {
    registerForm.style.display = 'none';
    loginForm.style.display = 'block';
    authTag.textContent = 'Terminal de operaciones de bodega';
    authError.style.display = 'none';
};

function setAuthBtnLoading(prefix, loading) {
    const btn = document.getElementById(prefix + '-submit');
    const label = btn.querySelector('.btn-label');
    const spinner = btn.querySelector('.btn-spinner');
    btn.disabled = loading;
    spinner.hidden = !loading;
    label.style.opacity = loading ? '0.6' : '1';
}

loginForm.onsubmit = async (e) => {
    e.preventDefault();
    state.apiBase = document.getElementById('api-base').value.trim();
    authError.style.display = 'none';
    setAuthBtnLoading('login', true);
    try {
        const data = await api('/auth/login', {
            method: 'POST', skipAuth: true,
            body: {
                username: document.getElementById('login-username').value,
                password: document.getElementById('login-password').value
            }
        });
        state.token = data.token; state.username = data.username; state.rol = data.rol;
        enterApp();
    } catch (err) {
        authError.textContent = err.status === 401
            ? 'Usuario o contrasena incorrectos.'
            : err.message;
        authError.style.display = 'block';
    } finally {
        setAuthBtnLoading('login', false);
    }
};

registerForm.onsubmit = async (e) => {
    e.preventDefault();
    state.apiBase = document.getElementById('api-base').value.trim();
    authError.style.display = 'none';
    const payload = {
        nombreCompleto: document.getElementById('reg-nombre').value,
        email: document.getElementById('reg-email').value,
        username: document.getElementById('reg-username').value,
        password: document.getElementById('reg-password').value,
        rol: 'EMPLEADO'
    };
    setAuthBtnLoading('register', true);
    try {
        await api('/auth/register', { method: 'POST', skipAuth: true, body: payload });
        toast('Cuenta creada. Ahora inicia sesion.', 'success');
        registerForm.reset();
        document.getElementById('btn-to-login').click();
    } catch (err) {
        authError.textContent = err.message;
        authError.style.display = 'block';
    } finally {
        setAuthBtnLoading('register', false);
    }
};

/* =========================================================
   NAVEGACION + RBAC
   ========================================================= */
const sidebar = document.getElementById('sidebar');
const sidebarOverlay = document.getElementById('sidebar-overlay');

document.getElementById('hamburger-btn').onclick = () => {
    sidebar.classList.toggle('open');
    sidebarOverlay.classList.toggle('active');
};
sidebarOverlay.onclick = closeSidebarMobile;
function closeSidebarMobile() {
    sidebar.classList.remove('open');
    sidebarOverlay.classList.remove('active');
}

const ADMIN_ONLY_SECTIONS = ['usuarios', 'auditoria'];

const sectionLoaders = {
    bodegas: loadBodegas,
    productos: loadProductos,
    movimientos: () => { loadMovimientos(); loadReporteGeneral(); },
    usuarios: loadUsuarios,
    auditoria: loadAuditoria
};

function showSection(name) {
    if (ADMIN_ONLY_SECTIONS.includes(name) && state.rol !== 'ADMIN') {
        showDenied();
        return;
    }
    document.querySelectorAll('.nav-item').forEach(b => b.classList.toggle('active', b.dataset.section === name));
    document.querySelectorAll('.section').forEach(s => s.classList.toggle('active', s.id === 'section-' + name));
    document.getElementById('topbar-title').textContent = name.charAt(0).toUpperCase() + name.slice(1);
    closeSidebarMobile();
    (sectionLoaders[name] || (() => {}))();
}
document.querySelectorAll('.nav-item').forEach(btn => { btn.onclick = () => showSection(btn.dataset.section); });

function enterApp() {
    document.getElementById('login-screen').style.display = 'none';
    document.getElementById('app-shell').classList.add('active');
    document.getElementById('topbar-username').textContent = state.username;
    const badge = document.getElementById('topbar-role-badge');
    badge.textContent = state.rol;
    badge.className = 'badge ' + (state.rol === 'ADMIN' ? 'badge-admin' : 'badge-empleado');

    const isAdmin = state.rol === 'ADMIN';
    document.querySelectorAll('.nav-item[data-admin-only]').forEach(btn => {
        btn.style.display = isAdmin ? 'flex' : 'none';
    });

    showSection('bodegas');
}

function logout() {
    state.token = null; state.username = null; state.rol = null;
    state.bodegasCache = []; state.productosCache = [];
    document.getElementById('app-shell').classList.remove('active');
    document.getElementById('login-screen').style.display = 'flex';
    loginForm.reset(); registerForm.reset();
}
document.getElementById('logout-btn').onclick = () => confirmAction('¿Cerrar la sesion actual?', async () => logout());

/* =========================================================
   BODEGAS
   ========================================================= */
async function loadBodegas(nombreFiltro) {
    renderLoading('bodegas-table-wrap');
    try {
        const path = nombreFiltro ? `/api/bodegas/buscar?nombre=${encodeURIComponent(nombreFiltro)}` : '/api/bodegas';
        const data = await api(path);
        state.bodegasCache = data;
        renderTable('bodegas-table-wrap', {
            columns: [
                { key: 'id', label: 'ID' },
                { key: 'nombre', label: 'Nombre' },
                { key: 'ubicacion', label: 'Ubicacion' },
                { key: 'capacidad', label: 'Capacidad' },
                { key: 'encargado', label: 'Encargado' },
                { label: 'Estado', render: b => `<span class="badge ${b.activo ? 'badge-ok' : 'badge-off'}">${b.activo ? 'Activa' : 'Inactiva'}</span>` }
            ],
            rows: data,
            emptyMessage: 'No hay bodegas registradas.',
            actions: b => `
                <button class="btn btn-sm btn-icon" onclick="openEditBodegaModal(${b.id})" title="Editar">✎</button>
                <button class="btn btn-sm btn-icon btn-danger" onclick="deleteBodega(${b.id}, '${escapeHtml(b.nombre)}')" title="Eliminar">🗑</button>
            `
        });
    } catch (err) { handleApiError(err, 'bodegas-table-wrap'); }
}

document.getElementById('btn-nueva-bodega').onclick = () => openBodegaModal();
document.getElementById('bodega-buscar').onclick = () => loadBodegas(document.getElementById('bodega-filtro-nombre').value.trim());
document.getElementById('bodega-buscar-ubicacion').onclick = () => loadBodegasByLocation(document.getElementById('bodega-filtro-ubicacion').value.trim());
document.getElementById('bodega-limpiar').onclick = () => {
    document.getElementById('bodega-filtro-nombre').value = '';
    document.getElementById('bodega-filtro-ubicacion').value = '';
    loadBodegas();
};

async function loadBodegasByLocation(ubicacion) {
    if (!ubicacion) {
        loadBodegas();
        return;
    }
    renderLoading('bodegas-table-wrap');
    try {
        const data = await api(`/api/bodegas/ubicacion?ubicacion=${encodeURIComponent(ubicacion)}`);
        state.bodegasCache = data;
        renderTable('bodegas-table-wrap', {
            columns: [
                { key: 'id', label: 'ID' },
                { key: 'nombre', label: 'Nombre' },
                { key: 'ubicacion', label: 'Ubicacion' },
                { key: 'capacidad', label: 'Capacidad' },
                { key: 'encargado', label: 'Encargado' },
                { label: 'Estado', render: b => `<span class="badge ${b.activo ? 'badge-ok' : 'badge-off'}">${b.activo ? 'Activa' : 'Inactiva'}</span>` }
            ],
            rows: data,
            emptyMessage: 'No hay bodegas en esa ubicacion.',
            actions: b => `
                <button class="btn btn-sm btn-icon" onclick="openEditBodegaModal(${b.id})" title="Editar">✎</button>
                <button class="btn btn-sm btn-icon btn-danger" onclick="deleteBodega(${b.id}, '${escapeHtml(b.nombre)}')" title="Eliminar">🗑</button>
            `
        });
    } catch (err) { handleApiError(err, 'bodegas-table-wrap'); }
}

function bodegaFormHtml(b = {}) {
    return `
        <div class="field"><label>Nombre</label><input type="text" id="bodega-nombre" value="${escapeHtml(b.nombre || '')}" required></div>
        <div class="field"><label>Ubicacion</label><input type="text" id="bodega-ubicacion" value="${escapeHtml(b.ubicacion || '')}" required></div>
        <div class="field"><label>Capacidad</label><input type="number" id="bodega-capacidad" min="1" value="${b.capacidad ?? ''}" required></div>
        <div class="field"><label>Encargado</label><input type="text" id="bodega-encargado" value="${escapeHtml(b.encargado || '')}" required></div>
        <div class="field-error" id="bodega-form-error"></div>
    `;
}

function openBodegaModal() {
    openModal({
        title: 'Nueva bodega',
        bodyHtml: bodegaFormHtml(),
        footerHtml: `
            <button class="btn btn-ghost" id="bodega-cancel">Cancelar</button>
            <button class="btn btn-primary" id="bodega-save">Crear bodega</button>
        `
    });
    document.getElementById('bodega-cancel').onclick = closeModal;
    document.getElementById('bodega-save').onclick = () => submitBodega(null);
}

function openEditBodegaModal(id) {
    const bodega = state.bodegasCache.find(b => b.id === id);
    if (!bodega) return;
    openModal({
        title: 'Editar bodega #' + id,
        bodyHtml: bodegaFormHtml(bodega),
        footerHtml: `
            <button class="btn btn-ghost" id="bodega-cancel">Cancelar</button>
            <button class="btn btn-primary" id="bodega-save">Guardar cambios</button>
        `
    });
    document.getElementById('bodega-cancel').onclick = closeModal;
    document.getElementById('bodega-save').onclick = () => submitBodega(id);
}

async function submitBodega(id) {
    const btn = document.getElementById('bodega-save');
    const errBox = document.getElementById('bodega-form-error');
    errBox.style.display = 'none';

    const payload = {
        nombre: document.getElementById('bodega-nombre').value.trim(),
        ubicacion: document.getElementById('bodega-ubicacion').value.trim(),
        capacidad: Number(document.getElementById('bodega-capacidad').value),
        encargado: document.getElementById('bodega-encargado').value.trim()
    };

    setBtnLoading(btn, true);
    try {
        if (id) {
            await api(`/api/bodegas/${id}`, { method: 'PUT', body: payload });
            toast('Bodega actualizada.', 'success');
        } else {
            await api('/api/bodegas', { method: 'POST', body: payload });
            toast('Bodega creada.', 'success');
        }
        closeModal();
        loadBodegas();
    } catch (err) {
        setBtnLoading(btn, false);
        if (err.status === 403) { closeModal(); showDenied(); return; }
        errBox.textContent = err.message;
        errBox.style.display = 'block';
    }
}

function deleteBodega(id, nombre) {
    confirmAction(`¿Eliminar la bodega "${nombre}"? Esta accion no se puede deshacer.`, async () => {
        await api(`/api/bodegas/${id}`, { method: 'DELETE' });
        toast('Bodega eliminada.', 'success');
        loadBodegas();
    });
}

/* =========================================================
   PRODUCTOS
   ========================================================= */
async function loadProductos(nombreFiltro) {
    renderLoading('productos-table-wrap');
    try {
        const path = nombreFiltro ? `/api/productos/buscar?nombre=${encodeURIComponent(nombreFiltro)}` : '/api/productos';
        const data = await api(path);
        state.productosCache = data;
        renderProductosTable(data);
    } catch (err) { handleApiError(err, 'productos-table-wrap'); }
}

async function loadProductosStockBajo() {
    renderLoading('productos-table-wrap');
    try {
        const data = await api('/api/productos/stock-bajo');
        renderProductosTable(data, 'No hay productos con stock bajo. 🎉');
    } catch (err) { handleApiError(err, 'productos-table-wrap'); }
}

function renderProductosTable(data, emptyMessage) {
    renderTable('productos-table-wrap', {
        columns: [
            { key: 'id', label: 'ID' },
            { key: 'nombre', label: 'Nombre' },
            { key: 'categoria', label: 'Categoria' },
            { label: 'Stock', render: p => p.stock < 10 ? `<span class="badge badge-off" style="color:var(--danger);background:var(--danger-soft)">${p.stock}</span>` : p.stock },
            { label: 'Precio', render: p => formatMoney(p.precio) }
        ],
        rows: data,
        emptyMessage: emptyMessage || 'No hay productos registrados.',
        actions: p => `
            <button class="btn btn-sm btn-icon" onclick="openEditProductoModal(${p.id})" title="Editar">✎</button>
            <button class="btn btn-sm btn-icon btn-danger" onclick="deleteProducto(${p.id}, '${escapeHtml(p.nombre)}')" title="Eliminar">🗑</button>
        `
    });
}

document.getElementById('btn-nuevo-producto').onclick = () => openProductoModal();
document.getElementById('producto-buscar').onclick = () => loadProductos(document.getElementById('producto-filtro-nombre').value.trim());
document.getElementById('producto-stock-bajo').onclick = () => loadProductosStockBajo();
document.getElementById('producto-filtro-precio-tipo').onchange = (e) => {
    document.getElementById('producto-filtro-precio-max').hidden = e.target.value !== 'entre';
};
document.getElementById('producto-filtrar-precio').onclick = loadProductosByPrice;
document.getElementById('producto-limpiar').onclick = () => {
    document.getElementById('producto-filtro-nombre').value = '';
    document.getElementById('producto-filtro-precio-tipo').value = '';
    document.getElementById('producto-filtro-precio').value = '';
    document.getElementById('producto-filtro-precio-max').value = '';
    document.getElementById('producto-filtro-precio-max').hidden = true;
    loadProductos();
};

async function loadProductosByPrice() {
    const tipo = document.getElementById('producto-filtro-precio-tipo').value;
    const precio = document.getElementById('producto-filtro-precio').value;
    const max = document.getElementById('producto-filtro-precio-max').value;
    const nombre = document.getElementById('producto-filtro-nombre').value.trim();
    if (!tipo || precio === '' || (tipo === 'entre' && max === '')) {
        toast('Selecciona un filtro y completa sus precios.', 'error');
        return;
    }
    let path;
    if (tipo === 'mayor-igual') path = `/api/productos/filtrar/precio-mayor-igual?precio=${encodeURIComponent(precio)}`;
    if (tipo === 'menor-igual') path = `/api/productos/filtrar/precio-menor-igual?precio=${encodeURIComponent(precio)}`;
    if (tipo === 'entre') path = `/api/productos/filtrar/precio-entre?min=${encodeURIComponent(precio)}&max=${encodeURIComponent(max)}`;
    if (tipo === 'nombre-y-precio') {
        if (!nombre) {
            toast('Escribe un nombre para combinarlo con el precio.', 'error');
            return;
        }
        path = `/api/productos/filtrar/nombre-y-precio?nombre=${encodeURIComponent(nombre)}&precio=${encodeURIComponent(precio)}`;
    }
    renderLoading('productos-table-wrap');
    try {
        const data = await api(path);
        state.productosCache = data;
        renderProductosTable(data);
    } catch (err) { handleApiError(err, 'productos-table-wrap'); }
}

function productoFormHtml(p = {}) {
    return `
        <div class="field"><label>Nombre</label><input type="text" id="producto-nombre" value="${escapeHtml(p.nombre || '')}" required></div>
        <div class="field"><label>Categoria</label><input type="text" id="producto-categoria" value="${escapeHtml(p.categoria || '')}" required></div>
        <div class="field-row">
            <div class="field"><label>Stock</label><input type="number" id="producto-stock" min="0" value="${p.stock ?? ''}" required></div>
            <div class="field"><label>Precio</label><input type="number" id="producto-precio" min="0" step="0.01" value="${p.precio ?? ''}" required></div>
        </div>
        <div class="field-error" id="producto-form-error"></div>
    `;
}

function openProductoModal() {
    openModal({
        title: 'Nuevo producto',
        bodyHtml: productoFormHtml(),
        footerHtml: `
            <button class="btn btn-ghost" id="producto-cancel">Cancelar</button>
            <button class="btn btn-primary" id="producto-save">Crear producto</button>
        `
    });
    document.getElementById('producto-cancel').onclick = closeModal;
    document.getElementById('producto-save').onclick = () => submitProducto(null);
}

function openEditProductoModal(id) {
    const producto = state.productosCache.find(p => p.id === id);
    if (!producto) return;
    openModal({
        title: 'Editar producto #' + id,
        bodyHtml: productoFormHtml(producto),
        footerHtml: `
            <button class="btn btn-ghost" id="producto-cancel">Cancelar</button>
            <button class="btn btn-primary" id="producto-save">Guardar cambios</button>
        `
    });
    document.getElementById('producto-cancel').onclick = closeModal;
    document.getElementById('producto-save').onclick = () => submitProducto(id);
}

async function submitProducto(id) {
    const btn = document.getElementById('producto-save');
    const errBox = document.getElementById('producto-form-error');
    errBox.style.display = 'none';

    const payload = {
        nombre: document.getElementById('producto-nombre').value.trim(),
        categoria: document.getElementById('producto-categoria').value.trim(),
        stock: Number(document.getElementById('producto-stock').value),
        precio: Number(document.getElementById('producto-precio').value)
    };

    setBtnLoading(btn, true);
    try {
        if (id) {
            await api(`/api/productos/${id}`, { method: 'PUT', body: payload });
            toast('Producto actualizado.', 'success');
        } else {
            await api('/api/productos', { method: 'POST', body: payload });
            toast('Producto creado.', 'success');
        }
        closeModal();
        loadProductos();
    } catch (err) {
        setBtnLoading(btn, false);
        if (err.status === 403) { closeModal(); showDenied(); return; }
        errBox.textContent = err.message;
        errBox.style.display = 'block';
    }
}

function deleteProducto(id, nombre) {
    confirmAction(`¿Eliminar el producto "${nombre}"? Esta accion no se puede deshacer.`, async () => {
        await api(`/api/productos/${id}`, { method: 'DELETE' });
        toast('Producto eliminado.', 'success');
        loadProductos();
    });
}

/* =========================================================
   MOVIMIENTOS
   ========================================================= */
async function loadMovimientos(inicio, fin) {
    renderLoading('movimientos-table-wrap');
    try {
        const path = (inicio && fin)
            ? `/api/movimientos/rango-fechas?inicio=${encodeURIComponent(inicio)}&fin=${encodeURIComponent(fin)}`
            : '/api/movimientos';
        const data = await api(path);
        renderTable('movimientos-table-wrap', {
            columns: [
                { key: 'id', label: 'ID' },
                { label: 'Fecha', render: m => formatDate(m.fecha) },
                { label: 'Tipo', render: m => `<span class="badge badge-${m.tipoMovimiento.toLowerCase()}">${m.tipoMovimiento}</span>` },
                { key: 'usuarioResponsable', label: 'Responsable' },
                { label: 'Origen → Destino', render: m => `${escapeHtml(m.bodegaOrigen || '—')} → ${escapeHtml(m.bodegaDestino || '—')}` },
                { label: 'Items', render: m => (m.detalles || []).length }
            ],
            rows: data,
            emptyMessage: 'No hay movimientos registrados.',
            actions: m => `<button class="btn btn-sm btn-icon" onclick='openMovimientoDetalleModal(${JSON.stringify(m).replace(/'/g, "&#39;")})' title="Ver detalle">👁</button>`
        });
    } catch (err) { handleApiError(err, 'movimientos-table-wrap'); }
}

async function loadReporteGeneral() {
    const stockWrap = document.getElementById('reporte-stock-wrap');
    const movidosWrap = document.getElementById('reporte-movidos-wrap');
    stockWrap.innerHTML = '<div class="loading-state"><span class="loading-spinner"></span></div>';
    movidosWrap.innerHTML = '<div class="loading-state"><span class="loading-spinner"></span></div>';
    try {
        const data = await api('/api/movimientos/reporte-general');
        const stock = data.stockPorBodega || [];
        const movidos = data.productosMasMovidos || [];

        stockWrap.innerHTML = stock.length
            ? `<ul class="stat-list">${stock.map(s => `<li><span>${escapeHtml(s.bodegaNombre)}</span><span class="stat-value">${s.stockTotal}</span></li>`).join('')}</ul>`
            : '<div class="empty-state">Sin datos aun.</div>';

        movidosWrap.innerHTML = movidos.length
            ? `<ul class="stat-list">${movidos.map(p => `<li><span>${escapeHtml(p.productoNombre)}</span><span class="stat-value">${p.totalMovido}</span></li>`).join('')}</ul>`
            : '<div class="empty-state">Sin datos aun.</div>';
    } catch (err) {
        renderError('reporte-stock-wrap', 'No se pudo cargar el reporte.');
        renderError('reporte-movidos-wrap', 'No se pudo cargar el reporte.');
    }
}

document.getElementById('mov-filtrar-fechas').onclick = () => {
    const inicio = toDateTimeSeconds(document.getElementById('mov-filtro-inicio').value);
    const fin = toDateTimeSeconds(document.getElementById('mov-filtro-fin').value);
    if (!inicio || !fin) { toast('Selecciona ambas fechas.', 'error'); return; }
    loadMovimientos(inicio, fin);
};
document.getElementById('mov-limpiar').onclick = () => {
    document.getElementById('mov-filtro-inicio').value = '';
    document.getElementById('mov-filtro-fin').value = '';
    loadMovimientos();
};

function openMovimientoDetalleModal(m) {
    const detalles = (m.detalles || []).map(d => `<tr><td>${escapeHtml(d.nombreProducto)}</td><td>${d.cantidad}</td></tr>`).join('');
    openModal({
        title: 'Movimiento #' + m.id,
        wide: true,
        bodyHtml: `
            <div class="detail-block"><div class="k">Tipo</div><div class="v"><span class="badge badge-${m.tipoMovimiento.toLowerCase()}">${m.tipoMovimiento}</span></div></div>
            <div class="detail-block"><div class="k">Fecha</div><div class="v">${formatDate(m.fecha)}</div></div>
            <div class="detail-block"><div class="k">Responsable</div><div class="v">${escapeHtml(m.usuarioResponsable)}</div></div>
            <div class="detail-block"><div class="k">Bodega origen</div><div class="v">${escapeHtml(m.bodegaOrigen || '—')}</div></div>
            <div class="detail-block"><div class="k">Bodega destino</div><div class="v">${escapeHtml(m.bodegaDestino || '—')}</div></div>
            <div class="detail-block"><div class="k">Observaciones</div><div class="v">${escapeHtml(m.observaciones || '—')}</div></div>
            <div class="detail-block">
                <div class="k">Productos</div>
                <table class="data-table diff-table"><thead><tr><th>Producto</th><th>Cantidad</th></tr></thead><tbody>${detalles}</tbody></table>
            </div>
        `,
        footerHtml: `<button class="btn btn-primary" id="mov-detalle-close">Cerrar</button>`
    });
    document.getElementById('mov-detalle-close').onclick = closeModal;
}

document.getElementById('btn-nuevo-movimiento').onclick = async () => {
    try {
        const [bodegas, productos] = await Promise.all([
            state.bodegasCache.length ? Promise.resolve(state.bodegasCache) : api('/api/bodegas'),
            state.productosCache.length ? Promise.resolve(state.productosCache) : api('/api/productos')
        ]);
        state.bodegasCache = bodegas;
        state.productosCache = productos;
        openMovimientoModal();
    } catch (err) { handleApiError(err); }
};

let detalleRowCount = 0;

function bodegaOptionsHtml(selected) {
    return state.bodegasCache.map(b => `<option value="${b.id}" ${String(b.id) === String(selected) ? 'selected' : ''}>${escapeHtml(b.nombre)}</option>`).join('');
}
function productoOptionsHtml(selected) {
    return state.productosCache.map(p => `<option value="${p.id}" ${String(p.id) === String(selected) ? 'selected' : ''}>${escapeHtml(p.nombre)} (stock: ${p.stock})</option>`).join('');
}

function detalleRowHtml(rowId) {
    return `
        <div class="detalle-row" data-row="${rowId}">
            <div class="field">
                <label>Producto</label>
                <select class="mov-detalle-producto">${productoOptionsHtml()}</select>
            </div>
            <div class="field qty-field">
                <label>Cantidad</label>
                <input type="number" class="mov-detalle-cantidad" min="1" value="1">
            </div>
            <button type="button" class="btn btn-icon btn-danger" onclick="removeDetalleRow(${rowId})" title="Quitar">✕</button>
        </div>
    `;
}

function removeDetalleRow(rowId) {
    const rows = document.querySelectorAll('.detalle-row');
    if (rows.length <= 1) return;
    document.querySelector(`.detalle-row[data-row="${rowId}"]`)?.remove();
}

function updateMovimientoBodegaVisibility() {
    const tipo = document.getElementById('mov-tipo').value;
    const origenField = document.getElementById('mov-origen-field');
    const destinoField = document.getElementById('mov-destino-field');
    origenField.style.display = (tipo === 'SALIDA' || tipo === 'TRANSFERENCIA') ? 'block' : 'none';
    destinoField.style.display = (tipo === 'ENTRADA' || tipo === 'TRANSFERENCIA') ? 'block' : 'none';
}

function openMovimientoModal() {
    detalleRowCount = 0;
    const firstRowId = detalleRowCount++;
    openModal({
        title: 'Registrar movimiento',
        wide: true,
        bodyHtml: `
            <div class="field">
                <label>Tipo de movimiento</label>
                <select id="mov-tipo">
                    <option value="ENTRADA">ENTRADA</option>
                    <option value="SALIDA">SALIDA</option>
                    <option value="TRANSFERENCIA">TRANSFERENCIA</option>
                </select>
            </div>
            <div class="field-row">
                <div class="field" id="mov-origen-field">
                    <label>Bodega origen</label>
                    <select id="mov-bodega-origen">${bodegaOptionsHtml()}</select>
                </div>
                <div class="field" id="mov-destino-field">
                    <label>Bodega destino</label>
                    <select id="mov-bodega-destino">${bodegaOptionsHtml()}</select>
                </div>
            </div>
            <div class="field"><label>Observaciones</label><input type="text" id="mov-observaciones" maxlength="255"></div>

            <div id="mov-detalles-wrap">${detalleRowHtml(firstRowId)}</div>
            <button type="button" class="btn btn-sm btn-ghost" id="mov-add-detalle">+ Agregar producto</button>
            <div class="field-error" id="mov-form-error" style="margin-top:12px;"></div>
        `,
        footerHtml: `
            <button class="btn btn-ghost" id="mov-cancel">Cancelar</button>
            <button class="btn btn-primary" id="mov-save">Registrar</button>
        `
    });

    document.getElementById('mov-tipo').onchange = updateMovimientoBodegaVisibility;
    updateMovimientoBodegaVisibility();

    document.getElementById('mov-add-detalle').onclick = () => {
        const rowId = detalleRowCount++;
        document.getElementById('mov-detalles-wrap').insertAdjacentHTML('beforeend', detalleRowHtml(rowId));
    };

    document.getElementById('mov-cancel').onclick = closeModal;
    document.getElementById('mov-save').onclick = submitMovimiento;
}

async function submitMovimiento() {
    const btn = document.getElementById('mov-save');
    const errBox = document.getElementById('mov-form-error');
    errBox.style.display = 'none';

    const tipo = document.getElementById('mov-tipo').value;
    const detalles = Array.from(document.querySelectorAll('.detalle-row')).map(row => ({
        productoId: Number(row.querySelector('.mov-detalle-producto').value),
        cantidad: Number(row.querySelector('.mov-detalle-cantidad').value)
    }));

    if (detalles.some(d => !d.productoId || !d.cantidad || d.cantidad < 1)) {
        errBox.textContent = 'Revisa las filas de productos: todas necesitan producto y cantidad valida.';
        errBox.style.display = 'block';
        return;
    }

    const payload = {
        tipoMovimiento: tipo,
        bodegaOrigenId: (tipo === 'SALIDA' || tipo === 'TRANSFERENCIA') ? Number(document.getElementById('mov-bodega-origen').value) : null,
        bodegaDestinoId: (tipo === 'ENTRADA' || tipo === 'TRANSFERENCIA') ? Number(document.getElementById('mov-bodega-destino').value) : null,
        observaciones: document.getElementById('mov-observaciones').value.trim() || null,
        detalles
    };

    setBtnLoading(btn, true);
    try {
        await api('/api/movimientos', { method: 'POST', body: payload });
        toast('Movimiento registrado.', 'success');
        closeModal();
        loadMovimientos();
        loadReporteGeneral();
        state.productosCache = []; // el stock cambio, refrescar cache en el proximo uso
    } catch (err) {
        setBtnLoading(btn, false);
        if (err.status === 403) { closeModal(); showDenied(); return; }
        errBox.textContent = err.message;
        errBox.style.display = 'block';
    }
}

/* =========================================================
   USUARIOS (solo ADMIN)
   ========================================================= */
async function loadUsuarios() {
    renderLoading('usuarios-table-wrap');
    try {
        const data = await api('/api/usuarios');
        renderUsuariosTable(data);
    } catch (err) { handleApiError(err, 'usuarios-table-wrap'); }
}

function renderUsuariosTable(data, emptyMessage) {
    renderTable('usuarios-table-wrap', {
        columns: [
            { key: 'id', label: 'ID' },
            { key: 'username', label: 'Username' },
            { key: 'nombreCompleto', label: 'Nombre' },
            { key: 'email', label: 'Email' },
            { label: 'Rol', render: u => `<span class="badge ${u.rol === 'ADMIN' ? 'badge-admin' : 'badge-empleado'}">${u.rol}</span>` },
            { label: 'Estado', render: u => `<span class="badge ${u.activo ? 'badge-ok' : 'badge-off'}">${u.activo ? 'Activo' : 'Inactivo'}</span>` }
        ],
        rows: data,
        emptyMessage: emptyMessage || 'No hay usuarios registrados.',
        actions: u => u.activo
            ? `<button class="btn btn-sm btn-danger" onclick="desactivarUsuario(${u.id}, '${escapeHtml(u.username)}')">Desactivar</button>`
            : `<span class="badge badge-off">Sin acciones</span>`
    });
}

document.getElementById('btn-nuevo-usuario').onclick = () => openUsuarioModal();
document.getElementById('usuario-buscar').onclick = async () => {
    const username = document.getElementById('usuario-filtro-username').value.trim();
    if (!username) { loadUsuarios(); return; }
    renderLoading('usuarios-table-wrap');
    try {
        const data = await api(`/api/usuarios/username/${encodeURIComponent(username)}`);
        renderUsuariosTable([data]);
    } catch (err) {
        if (err.status === 404) renderUsuariosTable([], 'Ningun usuario con ese username.');
        else handleApiError(err, 'usuarios-table-wrap');
    }
};
document.getElementById('usuario-filtrar-rol').onclick = async () => {
    const rol = document.getElementById('usuario-filtro-rol').value;
    if (!rol) { loadUsuarios(); return; }
    renderLoading('usuarios-table-wrap');
    try {
        const data = await api(`/api/usuarios/rol/${rol}`);
        renderUsuariosTable(data);
    } catch (err) { handleApiError(err, 'usuarios-table-wrap'); }
};
document.getElementById('usuario-limpiar').onclick = () => {
    document.getElementById('usuario-filtro-username').value = '';
    document.getElementById('usuario-filtro-rol').value = '';
    loadUsuarios();
};

function openUsuarioModal() {
    openModal({
        title: 'Nuevo usuario',
        bodyHtml: `
            <div class="field"><label>Nombre completo</label><input type="text" id="usuario-nombre" required></div>
            <div class="field"><label>Email</label><input type="email" id="usuario-email" required></div>
            <div class="field-row">
                <div class="field"><label>Username</label><input type="text" id="usuario-username" minlength="4" maxlength="50" required></div>
                <div class="field"><label>Contrasena</label><input type="password" id="usuario-password" minlength="6" required></div>
            </div>
            <div class="field">
                <label>Rol</label>
                <select id="usuario-rol">
                    <option value="EMPLEADO">EMPLEADO</option>
                    <option value="ADMIN">ADMIN</option>
                </select>
            </div>
            <div class="field-error" id="usuario-form-error"></div>
        `,
        footerHtml: `
            <button class="btn btn-ghost" id="usuario-cancel">Cancelar</button>
            <button class="btn btn-primary" id="usuario-save">Crear usuario</button>
        `
    });
    document.getElementById('usuario-cancel').onclick = closeModal;
    document.getElementById('usuario-save').onclick = submitUsuario;
}

async function submitUsuario() {
    const btn = document.getElementById('usuario-save');
    const errBox = document.getElementById('usuario-form-error');
    errBox.style.display = 'none';

    const payload = {
        nombreCompleto: document.getElementById('usuario-nombre').value.trim(),
        email: document.getElementById('usuario-email').value.trim(),
        username: document.getElementById('usuario-username').value.trim(),
        password: document.getElementById('usuario-password').value,
        rol: document.getElementById('usuario-rol').value
    };

    setBtnLoading(btn, true);
    try {
        await api('/api/usuarios', { method: 'POST', body: payload });
        toast('Usuario creado.', 'success');
        closeModal();
        loadUsuarios();
    } catch (err) {
        setBtnLoading(btn, false);
        if (err.status === 403) { closeModal(); showDenied(); return; }
        errBox.textContent = err.message;
        errBox.style.display = 'block';
    }
}

function desactivarUsuario(id, username) {
    confirmAction(`¿Desactivar al usuario "${username}"? Podra reactivarse mas adelante desde la base de datos.`, async () => {
        await api(`/api/usuarios/${id}/desactivar`, { method: 'PATCH' });
        toast('Usuario desactivado.', 'success');
        loadUsuarios();
    });
}

/* =========================================================
   AUDITORIA (solo ADMIN)
   ========================================================= */
async function loadAuditoria() {
    renderLoading('auditoria-table-wrap');
    try {
        const data = await api('/api/auditoria');
        renderAuditoriaTable(data);
    } catch (err) { handleApiError(err, 'auditoria-table-wrap'); }
}

function renderAuditoriaTable(data, emptyMessage) {
    renderTable('auditoria-table-wrap', {
        columns: [
            { key: 'id', label: 'ID' },
            { label: 'Tipo', render: a => `<span class="badge badge-${a.tipoOperacion.toLowerCase()}">${a.tipoOperacion}</span>` },
            { label: 'Fecha', render: a => formatDate(a.fechaHora) },
            { label: 'Usuario', render: a => escapeHtml(a.usuario || '—') },
            { key: 'entidadAfectada', label: 'Entidad' },
            { key: 'entidadId', label: 'Id entidad' }
        ],
        rows: data,
        emptyMessage: emptyMessage || 'No hay registros de auditoria.',
        actions: a => `<button class="btn btn-sm btn-icon" onclick='openAuditoriaDetalleModal(${JSON.stringify(a).replace(/'/g, "&#39;")})' title="Ver cambios">👁</button>`
    });
}

document.getElementById('audit-buscar-usuario').onclick = async () => {
    const usuarioId = document.getElementById('audit-filtro-usuario').value.trim();
    if (!usuarioId) { loadAuditoria(); return; }
    renderLoading('auditoria-table-wrap');
    try {
        const data = await api(`/api/auditoria/usuario/${encodeURIComponent(usuarioId)}`);
        renderAuditoriaTable(data);
    } catch (err) { handleApiError(err, 'auditoria-table-wrap'); }
};
document.getElementById('audit-filtrar-tipo').onclick = async () => {
    const tipo = document.getElementById('audit-filtro-tipo').value;
    if (!tipo) { loadAuditoria(); return; }
    renderLoading('auditoria-table-wrap');
    try {
        const data = await api(`/api/auditoria/tipo/${tipo}`);
        renderAuditoriaTable(data);
    } catch (err) { handleApiError(err, 'auditoria-table-wrap'); }
};
document.getElementById('audit-limpiar').onclick = () => {
    document.getElementById('audit-filtro-usuario').value = '';
    document.getElementById('audit-filtro-tipo').value = '';
    loadAuditoria();
};

function openAuditoriaDetalleModal(a) {
    const detalles = (a.detalles || []).map(d => `
        <tr>
            <td>${escapeHtml(d.campo)}</td>
            <td class="diff-old">${escapeHtml(d.valorAnterior ?? '—')}</td>
            <td class="diff-new">${escapeHtml(d.valorNuevo ?? '—')}</td>
        </tr>
    `).join('');

    openModal({
        title: `Auditoria #${a.id} — ${a.entidadAfectada} #${a.entidadId}`,
        wide: true,
        bodyHtml: `
            <div class="detail-block"><div class="k">Tipo de operacion</div><div class="v"><span class="badge badge-${a.tipoOperacion.toLowerCase()}">${a.tipoOperacion}</span></div></div>
            <div class="detail-block"><div class="k">Fecha</div><div class="v">${formatDate(a.fechaHora)}</div></div>
            <div class="detail-block"><div class="k">Usuario responsable</div><div class="v">${escapeHtml(a.usuario || 'No identificado')}</div></div>
            <div class="detail-block">
                <div class="k">Cambios registrados</div>
                <table class="data-table diff-table">
                    <thead><tr><th>Campo</th><th>Valor anterior</th><th>Valor nuevo</th></tr></thead>
                    <tbody>${detalles || '<tr><td colspan="3">Sin campos detallados.</td></tr>'}</tbody>
                </table>
            </div>
        `,
        footerHtml: `<button class="btn btn-primary" id="audit-detalle-close">Cerrar</button>`
    });
    document.getElementById('audit-detalle-close').onclick = closeModal;
}