<template>
  <v-app id="meetedit">
    <appmeetissues :meetIssues="app.meet_issues" :i18nData="i18n"></appmeetissues>
    <apptoolbar></apptoolbar>
    <v-content>
      <v-form v-model="validForm">
        <v-container fluid grid-list-lg style="max-width: 600px;">
          <v-layout row wrap>
            <v-flex xs12>
              <generaledit :meetData="app.meet" :meetTypes="app.meet_types" :i18nData="i18n" :locale="user.locale" :isLeader="isLeader"></generaledit>
            </v-flex>
            <v-flex xs12>
              <subjectslist :meetData="app.meet" :i18nData="i18n" :isLeader="isLeader"></subjectslist>
            </v-flex>
            <v-flex xs12>
              <attendantslist :meetData="app.meet" :i18nData="i18n" :isLeader="isLeader"></attendantslist>
            </v-flex>
            <v-flex xs12>
              <filesedit v-if="isLeader" :source="this" :meetData="app.meet" :i18nData="i18n" :isLeader="isLeader"></filesedit>
              <fileslist v-else :meetData="app.meet" :i18nData="i18n" :isLeader="isLeader"></fileslist>
            </v-flex>
          </v-layout>
        </v-container>
      </v-form>
    </v-content>
    <appfooter>
      <template slot="actions">
        <v-btn v-if="isLeader && app.meet.meet_state === MeetingState.EDIT" color="error" @click="execDelete">{{ i18n.btn_delmeeting}}</v-btn>
        <v-btn v-if="isLeader && app.meet.meet_state === MeetingState.READY" color="error" @click="execCancel">{{ i18n.btn_cancelmeeting}}</v-btn>
        <v-btn v-if="isLeader && app.meet.meet_state === MeetingState.CANCELLED" color="success" @click="execCreate">{{ i18n.btn_recovermeeting}}</v-btn>
        <v-btn v-if="isLeader && app.meet.meet_state === MeetingState.EDIT" color="success" @click="execCreate">{{ i18n.btn_register}}</v-btn>
        <v-btn v-if="isLeader && app.meet.meet_state === MeetingState.READY" color="success" @click="execStart">{{ i18n.btn_startmeeting}}</v-btn>

        <v-btn
          v-if="!isLeader"
          color="error"
          :disabled="attendantState == AttendState.REJECTED"
          @click="execReject">{{ i18n.btn_rejectmeeting}}</v-btn>
        <v-btn
          v-if="!isLeader && attendantState < AttendState.CONFIRMED"
          color="success"
          @click="execConfirm">{{ i18n.btn_confirmmeeting}}</v-btn>
        <v-btn
          v-if="!isLeader && attendantState == AttendState.CONFIRMED"
          color="success"
          :disabled="app.meet.meet_state !== MeetingState.STARTED"
          @click="execJoin">{{ i18n.btn_joinmeeting}}</v-btn>
      </template>
    </appfooter>
    <apperrortoast :message="ajaxerror" :i18n="i18n"></apperrortoast>
  </v-app>
</template>

<script>
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
import AppMeetIssues from '@/components/AppMeetIssues'
import MeetGeneralEdit from '@/components/MeetGeneralEdit'
import MeetFilesEdit from '@/components/MeetFilesEdit'
import MeetFilesList from '@/components/MeetFilesList'
import MeetSubjectsEdit from '@/components/MeetSubjectsEdit'
import MeetAttendantsEdit from '@/components/MeetAttendantsEdit'
import Meeting, { MeetingState } from '@/model/meeting'
import MeetingIssues from '@/model/meet_issue'
import { AttendState } from '@/model/attendant'

const appData = window.appData || {}
export default {
  name: 'MeetEdit',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter,
    'appmeetissues': AppMeetIssues,
    'generaledit': MeetGeneralEdit,
    'filesedit': MeetFilesEdit,
    'fileslist': MeetFilesList,
    'subjectslist': MeetSubjectsEdit,
    'attendantslist': MeetAttendantsEdit
  },

  computed: {
    isLeader: function () {
      return this.app.meet.meet_leader.user_id === this.user.user_id
    },
    attendantState: function () {
      let attendant = null
      this.app.meet.attendants.each((attd) => {
        if (attd.user_id.user_id === this.user.user_id) {
          attendant = attd
        }
      })
      return attendant == null ? AttendState.PENDING : attendant.attd_status
    }
  },

  data () {
    let meetIssues = this.$getModelInstance(MeetingIssues, appData.app.meet_issues)
    meetIssues.set('meet_id', appData.app.meet.meet_id)

    return {
      validForm: true,
      app: Object.assign(appData.app, {
        meet: this.$getModelInstance(Meeting, appData.app.meet),
        meet_issues: meetIssues
      }),
      AttendState: AttendState,
      MeetingState: MeetingState,
      user: Object.assign({
      }, appData.user),
      i18n: Object.assign({
        btn_addfile: 'Add file',
        btn_cancelmeeting: 'Cancel',
        btn_confirmmeeting: 'Confirm',
        btn_delfile: 'Remove file',
        btn_delattendant: 'Remove attendant',
        btn_delmeeting: 'Delete',
        btn_joinmeeting: 'Join',
        btn_startmeeting: 'Start',
        btn_recovermeeting: 'Recover',
        btn_register: 'Create',
        btn_rejectmeeting: 'Reject',
        error_unhandled: 'Unhandled error'
      }, appData.i18n)
    }
  },
  created: function () {
    this.$initWebsocket('/entity', () => {
      this.$socket.onmessage = (e) => {
        var data = JSON.parse(e.data)
        if (data.message === 'observe' && data.success === true) {
          // DO NOTHING
        } else if (data.message === 'update' && data.entity === 'meeting' && data.key === this.app.meet.meet_id) {
          this.$saveFetch(this.app.meet, this.isLeader)
          this.$saveFetch(this.app.meet_issues)
        }
      }

      this.$socket.sendObj({ message: 'observe', entity: 'meeting', key: this.app.meet.meet_id })
    })
  },

  methods: {
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
      })
    },
    execConfirm: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/attend/confirm', this.app.meet),
        method: 'POST'
      }).send()
    },
    execCreate: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/create', this.app.meet),
        method: 'POST'
      }).send().then((response) => {
        if (response.response.data.redirect) {
          window.location = response.response.data.redirect
        }
      })
    },
    execJoin: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/attend/join', this.app.meet),
        method: 'POST'
      }).send().then(() => { window.location = window.location })
    },
    execReject: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/attend/reject', this.app.meet),
        method: 'POST'
      }).send()
    },
    execStart: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/start', this.app.meet),
        method: 'POST'
      }).send().then((response) => {
        if (response.response.data.redirect) {
          window.location = response.response.data.redirect
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
