import AppErrorToast from '@/components/AppErrorToast'

let ModelAjax = function () {}

ModelAjax.install = function (Vue) {
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

  Vue.prototype.$_setModelError = function (responseError) {
    this.ajaxerror = null
    if (responseError != null) {
      let srverr = responseError.response.response.data.srverr
      if (srverr) {
        this.ajaxerror = srverr.error_message
      } else {
        this.ajaxerror = this.i18n.error_unhandled
      }
      setTimeout(() => { this.ajaxerror = null }, 3000)
    }
  }

  Vue.prototype.$saveFetch = function (model) {
    // Prevent reload while typing
    if (document.activeElement && (
      document.activeElement.tagName === 'TEXTAREA' ||
      document.activeElement.tagName === 'INPUT')
    ) {
      setTimeout(() => {
        this.$saveFetch(model)
      }, 500)
    } else {
      model.fetch()
    }
  }

  Vue.prototype.$getModelInstance = function (ModelClass, data) {
    let classInstance = new ModelClass(data)
    classInstance.on('save', (event) => {
      this.$_setModelError(event.error)
    })
    classInstance.on('delete', (event) => {
      this.$_setModelError(event.error)
    })
    classInstance.on('getRequest', (event) => {
      this.$_setModelError(event.error)
    })

    return classInstance
  }
}

export default ModelAjax
