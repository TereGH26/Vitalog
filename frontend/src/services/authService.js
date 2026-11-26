const csrfCookieName = 'XSRF-TOKEN';

function readCsrfCookie() {
  const cookie = document.cookie
    .split('; ')
    .find((entry) => entry.startsWith(`${csrfCookieName}=`));
  return cookie ? decodeURIComponent(cookie.slice(csrfCookieName.length + 1)) : '';
}

export async function refreshCsrfToken() {
  const response = await fetch('/api/csrf', { credentials: 'same-origin' });
  if (!response.ok) throw new Error('No se pudo preparar la conexión segura.');
  if (!readCsrfCookie()) throw new Error('No se recibió el token de seguridad.');
}

async function sendRequest(path, options = {}) {
  const method = options.method || 'GET';
  const headers = new Headers(options.headers || {});

  if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    if (!readCsrfCookie()) await refreshCsrfToken();
    headers.set('X-XSRF-TOKEN', readCsrfCookie());
  }

  const response = await fetch(path, {
    ...options,
    credentials: 'same-origin',
    headers,
  });

  if (response.status === 204) return null;

  const body = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(body.detail || body.message || 'No se pudo completar la solicitud.');
  }
  return body;
}

export async function getCurrentAccount() {
  const response = await fetch('/api/auth/me', { credentials: 'same-origin' });
  if (response.status === 401) return null;
  if (!response.ok) throw new Error('No se pudo consultar la sesión.');
  return response.json();
}

export function register(email, password) {
  return sendRequest('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
}

export async function login(email, password) {
  const form = new URLSearchParams({ email, password });
  await sendRequest('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: form,
  });
  await refreshCsrfToken();
}

export async function logout() {
  await sendRequest('/api/auth/logout', { method: 'POST' });
  await refreshCsrfToken();
}