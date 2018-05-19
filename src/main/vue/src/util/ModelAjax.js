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
        ajaxerror: null,
        lockFetch: {}
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

  Vue.prototype.$saveFetch = function (model, input) {
    if (
      // Prevent reload while typing
      (
        input &&
        document.activeElement && (
          document.activeElement.tagName === 'TEXTAREA' ||
          document.activeElement.tagName === 'INPUT'
        )
      ) ||
      this.lockFetch[model.getFetchURL()]
    ) {
      setTimeout(() => {
        this.$saveFetch(model, input)
      }, 500)
    } else {
      model.fetch()
    }
  }

  Vue.prototype.$lockFetch = function (model) {
    let lockLevel = this.lockFetch[model.getFetchURL()] || 0
    this.lockFetch[model.getFetchURL()] = lockLevel + 1
  }

  Vue.prototype.$unlockFetch = function (model) {
    let lockLevel = this.lockFetch[model.getFetchURL()] || 0
    if (lockLevel) {
      this.lockFetch[model.getFetchURL()] = lockLevel - 1
    } else {
      delete this.lockFetch[model.getFetchURL()]
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
