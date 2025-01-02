import { encrypt, decrypt } from './crypto';
//por ahora solo se quiere encriptar los datos de la sesion y nada mais
// se podria usar aca para encontrar mas cosas hay que simplemente llamarlas
//secureStorage.setItem('userData', {
//   id: 1,
//   name: 'John Doe'
// });
//
// // Recuperar datos
// const userData = secureStorage.getItem('userData');
//
// // Eliminar datos
// secureStorage.removeItem('userData');
export const secureStorage = {
  setItem(key, value) {
    const encryptedValue = encrypt(value);
    sessionStorage.setItem(key, encryptedValue);
  },

  getItem(key) {
    const encryptedValue = sessionStorage.getItem(key);
    if (!encryptedValue) return null;
    return decrypt(encryptedValue);
  },

  removeItem(key) {
    sessionStorage.removeItem(key);
  },

  clear() {
    sessionStorage.clear();
  }
};
