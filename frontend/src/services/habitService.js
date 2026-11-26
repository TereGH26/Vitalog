import { refreshCsrfToken } from './authService';

function readCsrfCookie() {
  const value = document.cookie
    .split('; ')
    .find((entry) => entry.startsWith('XSRF-TOKEN='));
  return value ? decodeURIComponent(value.slice('XSRF-TOKEN='.length)) : '';
}

async function sendJson(path, options) {
  if (!readCsrfCookie()) await refreshCsrfToken();
  const response = await fetch(path, {
    credentials: 'same-origin',
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'X-XSRF-TOKEN': readCsrfCookie(),
      ...(options.headers || {}),
    },
  });

  const body = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(body.detail || body.message || 'No se pudo guardar el hábito.');
  }
  return body;
}

export function getHabits() {
  return fetch('/api/habits', { credentials: 'same-origin' }).then(async (response) => {
    const body = await response.json().catch(() => []);
    if (!response.ok) throw new Error(body.message || 'No se pudieron consultar los hábitos.');
    return body;
  });
}

export function createHabit(habit) {
  return sendJson('/api/habits', {
    method: 'POST',
    body: JSON.stringify(habit),
  });
}
