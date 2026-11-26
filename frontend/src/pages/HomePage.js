import { useEffect, useState } from 'react';
import { createHabit, getHabits } from '../services/habitService';

const weekdays = [
  ['MONDAY', 'Lunes'], ['TUESDAY', 'Martes'], ['WEDNESDAY', 'Miércoles'],
  ['THURSDAY', 'Jueves'], ['FRIDAY', 'Viernes'], ['SATURDAY', 'Sábado'], ['SUNDAY', 'Domingo'],
];

function HomePage({ email, onLogout, error }) {
  const [name, setName] = useState('');
  const [durationDays, setDurationDays] = useState(66);
  const [frequency, setFrequency] = useState('DAILY');
  const [selectedDays, setSelectedDays] = useState([]);
  const [habits, setHabits] = useState([]);
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);
  const [isHabitFormOpen, setIsHabitFormOpen] = useState(false);

  useEffect(() => {
    let active = true;
    getHabits()
      .then((items) => { if (active) setHabits(items); })
      .catch((requestError) => { if (active) setFormError(requestError.message); });
    return () => { active = false; };
  }, []);

  function toggleDay(day) {
    setSelectedDays((current) => current.includes(day)
      ? current.filter((item) => item !== day)
      : [...current, day]);
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setFormError('');

    if (!name.trim()) {
      setFormError('Ingresa el nombre del hábito.');
      return;
    }
    if (name.trim().length > 100) {
      setFormError('El nombre del hábito debe tener 100 caracteres como máximo.');
      return;
    }
    if (!Number.isInteger(Number(durationDays)) || Number(durationDays) < 1) {
      setFormError('La duración debe ser de al menos 1 día.');
      return;
    }
    if (frequency === 'WEEKLY' && selectedDays.length === 0) {
      setFormError('Selecciona al menos un día para la frecuencia semanal.');
      return;
    }

    setSaving(true);
    try {
      const habit = await createHabit({
        name: name.trim(),
        durationDays: Number(durationDays),
        frequency,
        weekdays: frequency === 'WEEKLY' ? selectedDays : [],
      });
      setHabits((current) => [habit, ...current]);
      setName('');
      setDurationDays(66);
      setFrequency('DAILY');
      setSelectedDays([]);
      setIsHabitFormOpen(false);
    } catch (requestError) {
      setFormError(requestError.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="app-shell home-layout">
      <header className="home-header">
        <p className="eyebrow">VitaLog</p>
        <div className="account-actions">
          <span>{email}</span>
          <button className="secondary-button" onClick={onLogout} type="button">
            Cerrar sesión
          </button>
        </div>
      </header>
      {(error || formError) && <p className="form-error" role="alert">{formError || error}</p>}

      <div className="habit-toolbar">
        {!isHabitFormOpen && (
          <button
            className="primary-button habit-create-button"
            onClick={() => setIsHabitFormOpen(true)}
            type="button"
          >
            Crear hábito
          </button>
        )}
      </div>

      <section className="habit-layout">
        {isHabitFormOpen && (
          <div className="habit-form-panel">
            <p className="eyebrow">Nuevo hábito</p>
            <h1>Crear un hábito</h1>
            <form className="habit-form" onSubmit={handleSubmit}>
              <label htmlFor="habit-name">Nombre del hábito</label>
              <input id="habit-name" value={name} onChange={(event) => setName(event.target.value)} maxLength={100} placeholder="Ej. Beber agua" />

              <label htmlFor="habit-duration">Duración del seguimiento (días)</label>
              <input id="habit-duration" type="number" min="1" value={durationDays} onChange={(event) => setDurationDays(event.target.value)} />

              <fieldset>
                <legend>Frecuencia</legend>
                <label className="radio-option">
                  <input type="radio" name="frequency" value="DAILY" checked={frequency === 'DAILY'} onChange={() => setFrequency('DAILY')} />
                  Diaria
                </label>
                <label className="radio-option">
                  <input type="radio" name="frequency" value="WEEKLY" checked={frequency === 'WEEKLY'} onChange={() => setFrequency('WEEKLY')} />
                  Semanal
                </label>
              </fieldset>

              {frequency === 'WEEKLY' && (
                <fieldset>
                  <legend>Días de la semana</legend>
                  <div className="weekday-grid">
                    {weekdays.map(([value, label]) => (
                      <label className="weekday-option" key={value}>
                        <input type="checkbox" checked={selectedDays.includes(value)} onChange={() => toggleDay(value)} />
                        {label}
                      </label>
                    ))}
                  </div>
                </fieldset>
              )}

              <button className="primary-button" type="submit" disabled={saving}>
                {saving ? 'Guardando...' : 'Crear hábito'}
              </button>
            </form>
          </div>
        )}

        <div className="habit-list-panel">
          <p className="eyebrow">Mis hábitos</p>
          {habits.length === 0 ? (
            <p>Aún no tienes hábitos.</p>
          ) : (
            <ul className="habit-list">
              {habits.map((habit) => (
                <li key={habit.id}>
                  <strong>{habit.name}</strong>
                  <span>{habit.frequency === 'DAILY' ? 'Diaria' : `Semanal: ${habit.weekdays.join(', ')}`}</span>
                  <span>{habit.durationDays} días · En construcción</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </section>
    </main>
  );
}

export default HomePage;