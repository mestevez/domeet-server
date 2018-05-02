<template>
  <v-app id="meetinfo">
    <v-navigation-drawer
      fixed
      v-model="notificationsPanel"
      right
      clipped
      app
      permanent
      width="150"
    >
    </v-navigation-drawer>
    <apptoolbar></apptoolbar>
    <v-content>
      Hello info
    </v-content>
    <appfooter></appfooter>
    <apperrortoast :message="errorDialogMessage" :showErrorDialog="showErrorDialog" :i18n="i18n"></apperrortoast>
  </v-app>
</template>

<script>
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
import AppErrorToast from '@/components/AppErrorToast'
import Meeting from '@/model/meeting'

const appData = window.appData || {}
export default {
  name: 'MeetInfo',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter,
    'apperrortoast': AppErrorToast
  },

  data () {
    return {
      notificationsPanel: true,
      errorDialogMessage: '',
      showErrorDialog: false,
      app: Object.assign(appData.app, {
        meet: new Meeting(appData.app.meet)
      }),
      user: Object.assign({
      }, appData.user),
      i18n: Object.assign({
      }, appData.i18n)
    }
  },

  methods: {
    _evtRequestError: function (errorResponse) {
      let errdata = errorResponse.response.data
      let srverr = errdata.srverr

      if (srverr) {
        this.errorDialogMessage = srverr.error_message
      } else {
        this.errorDialogMessage = this.i18n.error_unhandled
      }

      this.showErrorDialog = false
      this.showErrorDialog = true
    }
  }
}
</script>

<style scoped>

</style>
