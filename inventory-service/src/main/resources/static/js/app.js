/* =========================================================
   ESTADO GLOBAL
   ========================================================= */
const state = {
    token: null, username: null, rol: null,
    apiBase: 'http://localhost:8080',
    bodegasCache: [], productosCache: [], usuariosCache: []
};

/* =========================================================
   CONEXIÓN API
   ========================================================= */
async function api(path, { method = 'GET', body } = {}) {
    const headers = { 'Content-Type': 'application/json' };
    if (state.token) headers['Authorization'] = 'Bearer ' + state.token;

    const res = await fetch(state.apiBase + path, {
        method, headers,
        body: body ? JSON.stringify(body) : undefined
    });

    if (res.status === 204) return null;
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Error en el servidor');
    return data;
}

/* =========================================================
   AUTH (LOGIN & REGISTER)
   ========================================================= */
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const authError = document.getElementById('auth-error');
const authTag = document.getElementById('auth-tag');

// Swapping forms
document.getElementById('btn-to-register').onclick = () => {
    loginForm.style.display = 'none';
    registerForm.style.display = 'block';
    authTag.textContent = 'Registro de nuevo empleado';
};
document.getElementById('btn-to-login').onclick = () => {
    registerForm.style.display = 'none';
    loginForm.style.display = 'block';
    authTag.textContent = 'Terminal de operaciones de bodega';
};

// Login
loginForm.onsubmit = async (e) => {
    e.preventDefault();
    state.apiBase = document.getElementById('api-base').value;
    try {
        const data = await api('/auth/login', {
            method: 'POST',
            body: {
                username: document.getElementById('login-username').value,
                password: document.getElementById('login-password').value
            }
        });
        state.token = data.token; state.username = data.username; state.rol = data.rol;
        enterApp();
    } catch (err) { authError.textContent = err.message; authError.style.display = 'block'; }
};

// Register
registerForm.onsubmit = async (e) => {
    e.preventDefault();
    state.apiBase = document.getElementById('api-base').value;
    const payload = {
        nombreCompleto: document.getElementById('reg-nombre').value,
        email: document.getElementById('reg-email').value,
        username: document.getElementById('reg-username').value,
        password: document.getElementById('reg-password').value,
        rol: 'EMPLEADO'
    };
    try {
        await api('/auth/register', { method: 'POST', body: payload });
        toast('Cuenta creada. Inicia sesión.', 'success');
        document.getElementById('btn-to-login').click();
    } catch (err) { authError.textContent = err.message; authError.style.display = 'block'; }
};

/* =========================================================
   NAVEGACIÓN
   ========================================================= */
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

function showSection(name) {
    document.querySelectorAll('.nav-item').forEach(b => b.classList.toggle('active', b.dataset.section === name));
    document.querySelectorAll('.section').forEach(s => s.classList.toggle('active', s.id === 'section-' + name));
    document.getElementById('topbar-title').textContent = name.toUpperCase();

    if (name === 'bodegas') loadBodegas();
    if (name === 'productos') loadProductos();
}

document.querySelectorAll('.nav-item').forEach(btn => {
    btn.onclick = () => showSection(btn.dataset.section);
});

/* =========================================================
   BODEGAS (CRUD COMPLETO)
   ========================================================= */
async function loadBodegas() {
    try {
        const data = await api('/api/bodegas');
        renderTable('bodegas-table-wrap', data, ['id', 'nombre', 'ubicacion', 'capacidad']);
    } catch (err) { toast(err.message, 'error'); }
}

function renderTable(containerId, data, keys) {
    const wrap = document.getElementById(containerId);
    if(!data.length) { wrap.innerHTML = '<p>No hay datos.</p>'; return; }
    let html = '<table class="data-table"><thead><tr>';
    keys.forEach(k => html += `<th>${k.toUpperCase()}</th>`);
    html += '<th></th></tr></thead><tbody>';
    data.forEach(row => {
        html += '<tr>';
        keys.forEach(k => html += `<td>${row[k]}</td>`);
        html += `<td><button class="btn btn-sm" onclick="alert('ID: ${row.id}')">Ver</button></td></tr>`;
    });
    html += '</tbody></table>';
    wrap.innerHTML = html;
}

/* =========================================================
   PRODUCTOS
   ========================================================= */
async function loadProductos() {
    try {
        const data = await api('/api/productos');
        renderTable('productos-table-wrap', data, ['id', 'nombre', 'categoria', 'stock', 'precio']);
    } catch (err) { toast(err.message, 'error'); }
}

/* =========================================================
   UI HELPERS
   ========================================================= */
function toast(msg, type) {
    const stack = document.getElementById('toast-stack');
    const el = document.createElement('div');
    el.className = 'toast';
    el.style.background = type === 'error' ? 'var(--danger)' : 'var(--ok)';
    el.textContent = msg;
    stack.appendChild(el);
    setTimeout(() => el.remove(), 3000);
}

document.getElementById('logout-btn').onclick = () => location.reload();