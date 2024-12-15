import {ref} from 'vue'

export function useAuth() {
  const showLoginMenu = ref(false)
  const showRegisterMenu = ref(false)

  const showLoginForm = () => {
    showLoginMenu.value = true
    showRegisterMenu.value = false
  }

  const showRegisterForm = () => {
    showRegisterMenu.value = true
    showLoginMenu.value = false
  }

  const goToHome = () => {
    showLoginMenu.value = false
    showRegisterMenu.value = false
    
  }

  return {
    showLoginMenu,
    showRegisterMenu,
    showLoginForm,
    showRegisterForm,
    goToHome
  }
}
