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
      <v-container fluid grid-list-lg style="max-width: 600px;">
        <v-layout row wrap>
          <v-flex xs12>
            <h3 class="display-2">{{ app.meet.meet_title }}</h3>
          </v-flex>
          <v-flex xs12>
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
          </v-flex>
          <v-flex xs12 v-for="(subject, index) in app.meet.subjects.toJSON()" :key="subject.subject_id">
            <v-card>
              <v-toolbar card prominent dark color="secondary">
                <v-toolbar-title class="body-3">{{ subject.subject_title }}</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-btn icon @click="toggleCollapse(index)">
                  <v-icon v-if="isCollapsed(index)">expand_more</v-icon>
                  <v-icon v-else>expand_less</v-icon>
                </v-btn>
              </v-toolbar>
              <template v-if="!isCollapsed(index)">
                <v-divider></v-divider>
                <v-card-text>
                  <v-container fluid grid-list-lg style="max-width: 600px;">
                    <v-layout row wrap>
                      <v-flex xs12>
                        <div v-if="subject.subject_time_end != null">
                          <div>{{ i18n.label_subject_time_start }}: {{ parseDate(subject.subject_time_start) }}</div>
                          <div>{{ i18n.label_subject_time_end }}: {{ parseDate(subject.subject_time_end) }}</div>
                          <div>{{ i18n.label_subject_duration }}: {{ subject.subject_duration }}</div>
                        </div>
                      </v-flex>

                      <v-flex xs12>
                        <noteslist
                          :subject="subject"
                          :noteType="SubjectNoteType.DECISION"
                          :title="i18n.title_subject_decisions"
                          :addLabel="i18n.btn_add_decision"
                          :nodata="i18n.label_nodecisions"
                          :i18nData="i18n"
                          :isLeader="isLeader"></noteslist>
                      </v-flex>
                      <v-flex xs12>
                        <noteslist
                          :subject="subject"
                          :noteType="SubjectNoteType.AGREEMENT"
                          :title="i18n.title_subject_agreements"
                          :addLabel="i18n.btn_add_agreement"
                          :nodata="i18n.label_noagreement"
                          :i18nData="i18n"
                          :isLeader="isLeader"></noteslist>
                      </v-flex>
                      <v-flex xs12>
                        <noteslist
                          :subject="subject"
                          :noteType="SubjectNoteType.UNSETTLED"
                          :title="i18n.title_subject_unsettled"
                          :addLabel="i18n.btn_add_unsettled"
                          :nodata="i18n.label_nounsettled"
                          :i18nData="i18n"
                          :isLeader="isLeader"></noteslist>
                      </v-flex>
                      <v-flex xs12>
                        <noteslist
                          :subject="subject"
                          :noteType="SubjectNoteType.COMMENT"
                          :title="i18n.title_subject_comments"
                          :addLabel="i18n.btn_add_comment"
                          :nodata="i18n.label_nocomment"
                          :i18nData="i18n"
                          :isLeader="isLeader"></noteslist>
                      </v-flex>
                    </v-layout>
                  </v-container>
                </v-card-text>
              </template>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
    <appfooter>
      <template slot="actions">
        <v-dialog v-if="isLeader" v-model="confirmDialog" persistent>
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
              <v-btn color="success" @click="execConclude(false)">{{ i18n.btn_confirm }}</v-btn>
              <v-btn color="success" @click="execConclude(true)">{{ i18n.btn_confirmandmail }}</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </template>
    </appfooter>
  </v-app>
</template>

<script>
import moment from 'moment'
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
import MeetSubjectNotesEdit from '@/components/MeetSubjectNotesEdit'
import Meeting from '@/model/meeting'
import { SubjectNoteType } from '@/model/subject_note'

const appData = window.appData || {}
export default {
  name: 'MeetConclude',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter,
    'noteslist': MeetSubjectNotesEdit
  },

  computed: {
    isLeader: function () {
      return this.app.meet.meet_leader.user_id === this.user.user_id
    }
  },

  data () {
    return {
      collapsed: 0,
      confirmDialog: false,
      notificationsPanel: true,
      SubjectNoteType: SubjectNoteType,
      app: Object.assign(appData.app, {
        meet: this.$getModelInstance(Meeting, appData.app.meet)
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
  created: function () {
    this.$initWebsocket('/entity', () => {
      this.$socket.onmessage = (e) => {
        var data = JSON.parse(e.data)
        if (data.message === 'observe' && data.success === true) {
          // DO NOTHING
        } else if (data.message === 'update' && data.entity === 'meeting' && data.key === this.app.meet.meet_id) {
          if (!this.isLeader) {
            this.app.meet.fetch()
          } else {
            this.$saveFetch(this.app.meet)
          }
        }
      }

      this.$socket.sendObj({ message: 'observe', entity: 'meeting', key: this.app.meet.meet_id })
    })
  },

  methods: {
    parseDate: function (isodate) {
      return moment(isodate).format(this.user.locale.timestampformat)
    },
    execConclude: function (sendmail) {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/conclude?send_mail=' + sendmail, this.app.meet),
        method: 'POST'
      }).send().then((response) => {
        if (response.response.data.redirect) {
          window.location = response.response.data.redirect
        }
      })
    },
    isCollapsed: function (index) {
      let bin = (this.collapsed >>> 0).toString(2).padStart(index, '0')
      let backwiseindex = bin.length - (index + 1)
      return Number.parseInt(bin.charAt(backwiseindex)) === 1
    },
    toggleCollapse: function (index) {
      let bin = (this.collapsed >>> 0).toString(2).padStart(index, '0')
      let backwiseindex = bin.length - (index + 1)
      bin = bin.substring(0, backwiseindex) + (this.isCollapsed(index) ? 0 : 1) + bin.substring(backwiseindex + 1)
      this.collapsed = parseInt(bin, 2).toString(10)
    }
  }
}
</script>

<style scoped>

</style>
