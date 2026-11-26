import { useState } from 'react';
import { getCurrentAccount, login, register } from '../services/authService';
import { getPasswordChecks } from '../utils/passwordValidation';

function AuthPage({ onAuthenticated, initialError = '' }) {
  const [mode, setMode] = useState('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmation, setConfirmation] = useState('');
  const [error, setError] = useState(initialError);
  const [submitting, setSubmitting] = useState(false);
  const isRegistering = mode === 'register';
  const passwordChecks = getPasswordChecks(password);

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    if (isRegistering) {
      if (!Object.values(passwordChecks).every(Boolean)) {
        setError('La contraseña todavía no cumple todos los requisitos.');
        return;
      }
      if (password !== confirmation) {
        setError('Las contraseñas no coinciden.');
        return;
      }
    }

    setSubmitting(true);
    try {
      if (isRegistering) await register(email, password);
      await login(email, password);
      const account = await getCurrentAccount();
      if (!account) throw new Error('No se pudo iniciar la sesión. Inténtalo de nuevo.');
      onAuthenticated(account);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setSubmitting(false);
    }
  }

  function changeMode(nextMode) {
    setMode(nextMode);
    setPassword('');
    setConfirmation('');
    setError('');
  }

  return (
    <main className="app-shell auth-layout">
      <section className="auth-panel" aria-labelledby="auth-title">
        <p className="eyebrow">VitaLog</p>
        <h1 id="auth-title">{isRegistering ? 'Crear cuenta' : 'Iniciar sesión'}</h1>
        <p className="auth-intro">
          {isRegistering
            ? 'Crea tu cuenta para guardar tu progreso.'
            : 'Continúa con tu cuenta de VitaLog.'}
        </p>

        <div className="auth-tabs" role="tablist" aria-label="Acceso a VitaLog">
          <button
            aria-selected={!isRegistering}
            className={!isRegistering ? 'active' : ''}
            onClick={() => changeMode('login')}
            role="tab"
            type="button"
          >
            Iniciar sesión
          </button>
          <button
            aria-selected={isRegistering}
            className={isRegistering ? 'active' : ''}
            onClick={() => changeMode('register')}
            role="tab"
            type="button"
          >
            Crear cuenta
          </button>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <label htmlFor="email">Correo electrónico</label>
          <input
            autoComplete="email"
            id="email"
            name="email"
            onChange={(event) => setEmail(event.target.value)}
            placeholder="nombre@correo.com"
            required
            type="email"
            value={email}
          />

          <label htmlFor="password">Contraseña</label>
          <input
            autoComplete={isRegistering ? 'new-password' : 'current-password'}
            id="password"
            name="password"
            onChange={(event) => setPassword(event.target.value)}
            required
            type="password"
            value={password}
          />

          {isRegistering && (
            <>
              <ul className="password-rules" aria-label="Requisitos de contraseña">
                <li className={passwordChecks.length ? 'valid' : ''}>8 caracteres como mínimo</li>
                <li className={passwordChecks.uppercase ? 'valid' : ''}>Una letra mayúscula</li>
                <li className={passwordChecks.lowercase ? 'valid' : ''}>Una letra minúscula</li>
                <li className={passwordChecks.number ? 'valid' : ''}>Un número</li>
                <li className={passwordChecks.special ? 'valid' : ''}>Un carácter especial</li>
              </ul>

              <label htmlFor="confirmation">Confirmar contraseña</label>
              <input
                autoComplete="new-password"
                id="confirmation"
                name="confirmation"
                onChange={(event) => setConfirmation(event.target.value)}
                required
                type="password"
                value={confirmation}
              />
            </>
          )}

          {error && <p className="form-error" role="alert">{error}</p>}

          <button className="primary-button" disabled={submitting} type="submit">
            {submitting
              ? 'Procesando...'
              : isRegistering
                ? 'Crear cuenta e ingresar'
                : 'Ingresar'}
          </button>
        </form>
      </section>
    </main>
  );
}

export default AuthPage;