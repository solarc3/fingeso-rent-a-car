import CryptoJS from 'crypto-js';

const SECRET_KEY = 'bolas123';

export const encrypt = (data) => {
  try {
    return CryptoJS.AES.encrypt(JSON.stringify(data), SECRET_KEY).toString();
  } catch (error) {
    console.error('Error encrypting data:', error);
    return null;
  }
};

export const decrypt = (encryptedData) => {
  try {
    const bytes = CryptoJS.AES.decrypt(encryptedData, SECRET_KEY);
    const decryptedString = bytes.toString(CryptoJS.enc.Utf8);
    return JSON.parse(decryptedString);
  } catch (error) {
    console.error('Error decrypting data:', error);
    return null;
  }
};
