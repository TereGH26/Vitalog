import { useEffect, useState } from 'react';
import AuthPage from './pages/AuthPage';
import HomePage from './pages/HomePage';
import { getCurrentAccount, logout, refreshCsrfToken } from './services/authService';
import './App.css';

function App() {
  const [account, setAccount] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    async function restoreSession() {
      try {
        await refreshCsrfToken();
        const currentAccount = await getCurrentAccount();
        if (active) setAccount(currentAccount);
      } catch (requestError) {
        if (active) setError(requestError.message);
      } finally {
        if (active) setLoading(false);
      }
    }

    restoreSession();
    return () => {
      active = false;
    };
  }, []);

  async function handleLogout() {
    try {
      await logout();
      setAccount(null);
      setError('');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  if (loading) {
    return <main className="app-shell"><p role="status">Cargando VitaLog...</p></main>;
  }

  if (account) {
    return <HomePage email={account.email} onLogout={handleLogout} error={error} />;
  }

  return <AuthPage onAuthenticated={setAccount} initialError={error} />;
}

export default App;