<template>
  <v-app id="meetedit">
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
      <v-form v-model="validForm">
        <v-container fluid grid-list-lg>
          <v-layout row wrap>
            <v-flex xs12>
              <generaledit :meetData="app.meet" :meetTypes="app.meet_types" :i18nData="i18n" :locale="user.locale"></generaledit>
            </v-flex>
            <v-flex xs12>
              <subjectslist :meetData="app.meet" :i18nData="i18n"></subjectslist>
            </v-flex>
            <v-flex xs12>
              <attendantslist :meetData="app.meet" :i18nData="i18n"></attendantslist>
            </v-flex>
            <v-flex xs12>
              <v-card>
                <v-card-title primary-title>
                  <v-container flex xs12>
                    <div class="headline">{{ i18n.title_files }}</div>
                  </v-container>
                </v-card-title>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-form>
    </v-content>
    <appfooter>
      <template slot="actions">
        <v-btn v-if="app.meet.meet_state === MeetingState.EDIT" color="error" @click="execDelete">{{ i18n.btn_delmeeting}}</v-btn>
        <v-btn v-if="app.meet.meet_state === MeetingState.READY" color="error" @click="execCancel">{{ i18n.btn_cancelmeeting}}</v-btn>
        <v-btn v-if="app.meet.meet_state === MeetingState.CANCELLED" color="success" @click="execCreate">{{ i18n.btn_recovermeeting}}</v-btn>
        <v-btn v-if="app.meet.meet_state === MeetingState.EDIT" color="success" @click="execCreate">{{ i18n.btn_register}}</v-btn>
        <v-btn v-if="app.meet.meet_state === MeetingState.READY" color="success" @click="execStart">{{ i18n.btn_startmeeting}}</v-btn>
      </template>
    </appfooter>
    <apperrortoast :message="errorDialogMessage" :showErrorDialog="showErrorDialog" :i18n="i18n"></apperrortoast>
  </v-app>
</template>

<script>
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
import AppErrorToast from '@/components/AppErrorToast'
import MeetGeneralEdit from '@/components/MeetGeneralEdit'
import MeetSubjectsEdit from '@/components/MeetSubjectsEdit'
import MeetAttendantsEdit from '@/components/MeetAttendantsEdit'
import Meeting, { MeetingState } from '@/model/meeting'

const appData = window.appData || {}
export default {
  name: 'MeetEdit',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter,
    'apperrortoast': AppErrorToast,
    'generaledit': MeetGeneralEdit,
    'subjectslist': MeetSubjectsEdit,
    'attendantslist': MeetAttendantsEdit
  },

  data () {
    return {
      notificationsPanel: true,
      validForm: true,
      errorDialogMessage: '',
      showErrorDialog: false,
      app: Object.assign(appData.app, {
        meet: new Meeting(appData.app.meet)
      }),
      MeetingState: MeetingState,
      user: Object.assign({
      }, appData.user),
      i18n: Object.assign({
        title_files: 'Files',
        label_search_user: 'Search user',
        btn_addfile: 'Add file',
        btn_delfile: 'Remove file',
        btn_delattendant: 'Remove attendant',
        btn_delmeeting: 'Delete',
        btn_cancelmeeting: 'Cancel',
        btn_startmeeting: 'Start',
        btn_recovermeeting: 'Recover',
        btn_register: 'Create',
        error_unhandled: 'Unhandled error'
      }, appData.i18n)
    }
  },

  created () {
    this.app.meet.on('save', (event) => {
      if (event.error) {
        this._evtRequestError(event.error.response)
      }
    })
    this.app.meet.on('delete', (event) => {
      if (event.error) {
        this._evtRequestError(event.error.response)
      }
    })
    this.app.meet.on('getRequest', (event) => {
      console.log('request')
      if (event.error) {
        this._evtRequestError(event.error.response)
      }
    })
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
    },
    execDelete: function () {
      this.app.meet.delete().then((response) => {
        if (response.response.data.redirect) {
          window.location = response.response.data.redirect
        }
      })
    },
    execCancel: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/cancel', this.app.meet),
        method: 'POST'
      }).send().then((response) => {
        if (response.response.data.redirect) {
          window.location = response.response.data.redirect
        }
      }).catch((error) => {
        this._evtRequestError(error.response)
      })
    },
    execCreate: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/create', this.app.meet),
        method: 'POST'
      }).send().then((response) => {
        if (response.response.data.redirect) {
          window.location = response.response.data.redirect
        }
      }).catch((error) => {
        this._evtRequestError(error.response)
      })
    },
    execStart: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/start', this.app.meet),
        method: 'POST'
      }).send().then((response) => {
        if (response.response.data.redirect) {
          window.location = response.response.data.redirect
        }
      }).catch((error) => {
        this._evtRequestError(error.response)
      })
    }
  }
}
</script>

<style scoped>

</style>
