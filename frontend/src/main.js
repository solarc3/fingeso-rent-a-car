import {createApp} from 'vue'
import {createPinia} from 'pinia'
import App from './App.vue'
import {registerPlugins} from '@/plugins'
import './assets/css/global.css'

// Create Vue app
const app = createApp(App)

// Create and use Pinia
const pinia = createPinia()
app.use(pinia)

// Register plugins
registerPlugins(app)

// Mount app
app.mount('#app')
