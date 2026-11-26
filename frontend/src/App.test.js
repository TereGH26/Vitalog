import '@testing-library/jest-dom';
import { fireEvent, render, screen } from '@testing-library/react';
import AuthPage from './pages/AuthPage';
import HomePage from './pages/HomePage';
import { createHabit, getHabits } from './services/habitService';
import { getPasswordChecks } from './utils/passwordValidation';

jest.mock('./services/habitService', () => ({
  createHabit: jest.fn(),
  getHabits: jest.fn(),
}));

test('muestra el formulario para ingresar con correo y contraseña', () => {
  render(<AuthPage onAuthenticated={() => {}} />);
  expect(screen.getByRole('heading', { name: 'Iniciar sesión' })).toBeInTheDocument();
  expect(screen.getByLabelText('Correo electrónico')).toHaveAttribute('type', 'email');
  expect(screen.getByLabelText('Contraseña')).toHaveAttribute('type', 'password');
});

test('muestra los cinco requisitos al crear una cuenta', () => {
  render(<AuthPage onAuthenticated={() => {}} />);
  fireEvent.click(screen.getByRole('tab', { name: 'Crear cuenta' }));
  expect(screen.getByText('8 caracteres como mínimo')).toBeInTheDocument();
  expect(screen.getByText('Una letra mayúscula')).toBeInTheDocument();
  expect(screen.getByText('Una letra minúscula')).toBeInTheDocument();
  expect(screen.getByText('Un número')).toBeInTheDocument();
  expect(screen.getByText('Un carácter especial')).toBeInTheDocument();
});

test('valida una contraseña con los cinco criterios requeridos', () => {
  expect(Object.values(getPasswordChecks('VitaLog1!')).every(Boolean)).toBe(true);
  expect(Object.values(getPasswordChecks('vitalog1')).every(Boolean)).toBe(false);
});

test('oculta el formulario al entrar y lo muestra al pulsar Crear hábito', () => {
  getHabits.mockResolvedValue([]);
  render(<HomePage email="ana@example.com" onLogout={() => {}} />);

  expect(screen.queryByLabelText('Nombre del hábito')).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole('button', { name: 'Crear hábito' }));
  expect(screen.getByLabelText('Nombre del hábito')).toBeInTheDocument();
});

test('muestra duración inicial de 66 días y frecuencia diaria', () => {
  getHabits.mockResolvedValue([]);
  render(<HomePage email="ana@example.com" onLogout={() => {}} />);
  fireEvent.click(screen.getByRole('button', { name: 'Crear hábito' }));
  expect(screen.getByLabelText('Duración del seguimiento (días)')).toHaveValue(66);
  expect(screen.getByRole('radio', { name: 'Diaria' })).toBeChecked();
});

test('rechaza un hábito semanal sin días seleccionados', async () => {
  getHabits.mockResolvedValue([]);
  render(<HomePage email="ana@example.com" onLogout={() => {}} />);
  fireEvent.click(screen.getByRole('button', { name: 'Crear hábito' }));

  fireEvent.change(screen.getByLabelText('Nombre del hábito'), { target: { value: 'Correr' } });
  fireEvent.click(screen.getByRole('radio', { name: 'Semanal' }));
  fireEvent.click(screen.getByRole('button', { name: 'Crear hábito' }));

  expect(await screen.findByRole('alert')).toHaveTextContent('Selecciona al menos un día');
  expect(createHabit).not.toHaveBeenCalled();
});

test('oculta el formulario después de crear el hábito', async () => {
  getHabits.mockResolvedValue([]);
  createHabit.mockResolvedValue({
    id: 'habit-1',
    name: 'Beber agua',
    durationDays: 66,
    frequency: 'DAILY',
    weekdays: [],
  });

  render(<HomePage email="ana@example.com" onLogout={() => {}} />);
  fireEvent.click(screen.getByRole('button', { name: 'Crear hábito' }));
  fireEvent.change(screen.getByLabelText('Nombre del hábito'), { target: { value: 'Beber agua' } });
  fireEvent.click(screen.getByRole('button', { name: 'Crear hábito' }));

  expect(await screen.findByText('Beber agua')).toBeInTheDocument();
  expect(screen.queryByLabelText('Nombre del hábito')).not.toBeInTheDocument();
});

test('mantiene el formulario abierto cuando falla la creación del hábito', async () => {
  getHabits.mockResolvedValue([]);
  createHabit.mockRejectedValue(new Error('No se pudo guardar el hábito.'));

  render(<HomePage email="ana@example.com" onLogout={() => {}} />);
  fireEvent.click(screen.getByRole('button', { name: 'Crear hábito' }));
  fireEvent.change(screen.getByLabelText('Nombre del hábito'), { target: { value: 'Meditar' } });
  fireEvent.click(screen.getByRole('button', { name: 'Crear hábito' }));

  expect(await screen.findByRole('alert')).toHaveTextContent('No se pudo guardar el hábito.');
  expect(screen.getByLabelText('Nombre del hábito')).toHaveValue('Meditar');
  expect(screen.getByLabelText('Nombre del hábito')).toBeInTheDocument();
});