<template>
  <v-app id="meetconclude">
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
      <h3 class="display-2">{{ app.meet.meet_title }}</h3>
      <v-card>
        <v-card-title>
          <div>
            <h3 class="headline mb-0">{{ i18n.title_general_info }}</h3>
            <div>
              <div>{{ i18n.label_meet_time_start }}: {{ parseDate(app.meet.meet_time_start) }}</div>
              <div>{{ i18n.label_meet_time_end }}: {{ parseDate(app.meet.meet_time_end) }}</div>
              <div>{{ i18n.label_meet_duration }}: {{ app.meet.meet_duration }}</div>
            </div>
          </div>
        </v-card-title>
      </v-card>
      <v-card v-for="(subject) in app.meet.subjects" :key="subject.subject_id">
        <v-card-title>
          <div>
            <h3 class="headline mb-0">{{ subject.subject_title }}</h3>
            <div v-if="subject.subject_time_end != null">
              <div>{{ i18n.label_subject_time_start }}: {{ parseDate(subject.subject_time_start) }}</div>
              <div>{{ i18n.label_subject_time_end }}: {{ parseDate(subject.subject_time_end) }}</div>
              <div>{{ i18n.label_subject_duration }}: {{ subject.subject_duration }}</div>
            </div>
          </div>
        </v-card-title>
        <noteslist
          :subject="subject"
          :noteType="SubjectNoteType.DECISION"
          :title="i18n.title_subject_decisions"
          :addLabel="i18n.btn_add_decision"
          :nodata="i18n.label_nodecisions"
          :i18nData="i18n"></noteslist>
        <noteslist
          :subject="subject"
          :noteType="SubjectNoteType.AGREEMENT"
          :title="i18n.title_subject_agreements"
          :addLabel="i18n.btn_add_agreement"
          :nodata="i18n.label_noagreement"
          :i18nData="i18n"></noteslist>
        <noteslist
          :subject="subject"
          :noteType="SubjectNoteType.UNSETTLED"
          :title="i18n.title_subject_unsettled"
          :addLabel="i18n.btn_add_unsettled"
          :nodata="i18n.label_nounsettled"
          :i18nData="i18n"></noteslist>
        <noteslist
          :subject="subject"
          :noteType="SubjectNoteType.COMMENT"
          :title="i18n.title_subject_comments"
          :addLabel="i18n.btn_add_comment"
          :nodata="i18n.label_nocomment"
          :i18nData="i18n"></noteslist>
      </v-card>
    </v-content>
    <appfooter>
      <template slot="actions">
        <v-dialog v-model="confirmDialog" persistent>
          <v-btn color="secondary" slot="activator">{{ i18n.btn_concludemeeting}}</v-btn>
          <v-card>
            <v-toolbar dark color="primary">
              <v-tooltip bottom class="ma-0">
                <v-btn
                  icon
                  large
                  slot="activator"
                  @click="confirmDialog = !confirmDialog"
                >
                  <v-icon large>keyboard_arrow_left</v-icon>
                </v-btn>
                <span>{{ i18n.btn_deny }}</span>
              </v-tooltip>
              <v-toolbar-title>{{ i18n.title_confirm }}</v-toolbar-title>
            </v-toolbar>
            <v-card-title primary-title>
              <div>
                <h3 class="headline mb-0">{{ i18n.confirm_msg_main }}</h3>
                <div>{{ i18n.confirm_msg_secondary }}</div>
              </div>
            </v-card-title>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="error" @click="confirmDialog = !confirmDialog">{{ i18n.btn_deny }}</v-btn>
              <v-btn color="success" @click="execConclude">{{ i18n.btn_confirm }}</v-btn>
              <v-btn color="success" @click="execConclude(true)">{{ i18n.btn_confirmandmail }}</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </template>
    </appfooter>
    <apperrortoast :message="errorDialogMessage" :showErrorDialog="showErrorDialog" :i18n="i18n"></apperrortoast>
  </v-app>
</template>

<script>
import moment from 'moment'
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
import AppErrorToast from '@/components/AppErrorToast'
import MeetSubjectNotesEdit from '@/components/MeetSubjectNotesEdit'
import Meeting from '@/model/meeting'
import { SubjectNoteType } from '@/model/subject_note'

const appData = window.appData || {}
export default {
  name: 'MeetConclude',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter,
    'apperrortoast': AppErrorToast,
    'noteslist': MeetSubjectNotesEdit
  },

  data () {
    return {
      confirmDialog: false,
      notificationsPanel: true,
      errorDialogMessage: '',
      showErrorDialog: false,
      SubjectNoteType: SubjectNoteType,
      app: Object.assign(appData.app, {
        meet: new Meeting(appData.app.meet)
      }),
      user: Object.assign({
      }, appData.user),
      i18n: Object.assign({
        btn_concludemeeting: 'Conclude',
        btn_deny: 'No',
        btn_confirm: 'Yes',
        btn_confirmandmail: 'Yes, and send Email',
        confirm_msg_main: 'Are you sure to conclude the meeting?',
        confirm_msg_secondary: 'You are going to conclude the meeting. After this operation it will not be able to apply any modification. You can also send the Minutes of Meeting to all invited users.',
        label_meet_time_start: 'Begin date',
        label_meet_time_end: 'End date',
        label_meet_duration: 'Duration',
        label_subject_time_start: 'Begin date',
        label_subject_time_end: 'End date',
        label_subject_duration: 'Duration',
        title_general_info: 'General info',
        title_confirm: 'Confirm conclusion',
        btn_add_decision: 'Add decision',
        btn_add_agreement: 'Add agreement',
        btn_add_unsettled: 'Add issue',
        btn_add_comment: 'Add comment',
        label_nodecisions: 'It looks like you have not taken any decision yet. Add one or more decisions for the executing subject.',
        label_noagreement: 'It looks like you have not reach any agreement yet. Add one or more agreements for the executing subject.',
        label_nounsettled: 'There is not pending issues',
        label_nocomment: 'No comments',
        title_subject_decisions: 'Decisions Taken',
        title_subject_agreements: 'Agreements',
        title_subject_unsettled: 'Pending issues',
        title_subject_comments: 'Comments'
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
    },
    parseDate: function (isodate) {
      return moment(isodate).format(this.user.locale.timestampformat)
    },
    execConclude: function (sendmail) {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/conclude', this.app.meet),
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
