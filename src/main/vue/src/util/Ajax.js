import axios from 'axios'
import AppErrorToast from '@/components/AppErrorToast'

axios.interceptors.response.use((response) => {
  return response
}, (error) => {
  return Promise.reject(error)
})

let Ajax = function () {}

Ajax.install = function (Vue) {
  // Inject some component options
  Vue.mixin({
    components: {
      'apperrortoast': AppErrorToast
    },
    data () {
      return {
        ajaxerror: null
      }
    }
  })

  Vue.prototype.$get = function () {
    return axios.get.apply(this, arguments).catch((error) => {
      this.ajaxerror = error
    })
  }
  Vue.prototype.$post = function () {
    return axios.post.apply(this, arguments).catch((error) => {
      this.ajaxerror = error
    })
  }
}

export default Ajax
