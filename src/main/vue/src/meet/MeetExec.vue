<template>
  <v-app id="meetexec">
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
      <v-layout column>
        <v-flex xs11>
          <h3 class="display-2">
            {{ app.meet.meet_title }}
            <template v-if="executingSubject != null"> - {{ executingSubject.subject_title }}</template>
          </h3>
          <attendantslist :meetData="app.meet" :i18nData="i18n"></attendantslist>
          <subjectslist :meetData="app.meet" :i18nData="i18n"></subjectslist>
          <!--<fileslist :meetData="app.meet" :i18nData="i18n"></fileslist>-->
          <noteslist
            v-if="executingSubject != null"
            :subject="executingSubject"
            :noteType="SubjectNoteType.DECISION"
            :title="i18n.title_subject_decisions"
            :addLabel="i18n.btn_add_decision"
            :nodata="i18n.label_nodecisions"
            :i18nData="i18n"></noteslist>
          <noteslist
            v-if="executingSubject != null"
            :subject="executingSubject"
            :noteType="SubjectNoteType.AGREEMENT"
            :title="i18n.title_subject_agreements"
            :addLabel="i18n.btn_add_agreement"
            :nodata="i18n.label_noagreement"
            :i18nData="i18n"></noteslist>
          <noteslist
            v-if="executingSubject != null"
            :subject="executingSubject"
            :noteType="SubjectNoteType.UNSETTLED"
            :title="i18n.title_subject_unsettled"
            :addLabel="i18n.btn_add_unsettled"
            :nodata="i18n.label_nounsettled"
            :i18nData="i18n"></noteslist>
          <noteslist
            v-if="executingSubject != null"
            :subject="executingSubject"
            :noteType="SubjectNoteType.COMMENT"
            :title="i18n.title_subject_comments"
            :addLabel="i18n.btn_add_comment"
            :nodata="i18n.label_nocomment"
            :i18nData="i18n"></noteslist>
        </v-flex>
      </v-layout>
    </v-content>
    <appfooter>
      <template slot="actions">
        <v-btn dark color="secondary" @click="execEnd">{{ i18n.btn_endmeeting}}</v-btn>
      </template>
    </appfooter>
  </v-app>
</template>

<script>
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
import MeetAttendantsList from '@/components/MeetAttendantsList'
import MeetSubjectsList from '@/components/MeetSubjectsList'
import MeetFilesList from '@/components/MeetFilesList'
import MeetSubjectNotesEdit from '@/components/MeetSubjectNotesEdit'
import Meeting from '@/model/meeting'
import { SubjectNoteType } from '@/model/subject_note'

const appData = window.appData || {}
export default {
  name: 'MeetExec',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter,
    'attendantslist': MeetAttendantsList,
    'subjectslist': MeetSubjectsList,
    'fileslist': MeetFilesList,
    'noteslist': MeetSubjectNotesEdit
  },

  computed: {
    executingSubject: function () {
      return this.app.meet.subjects.find((subject) => {
        return subject.subject_time_start != null && subject.subject_time_end == null
      })
    }
  },

  data () {
    return {
      notificationsPanel: true,
      SubjectNoteType: SubjectNoteType,
      app: Object.assign(appData.app, {
        meet: this.$getModelInstance(Meeting, appData.app.meet)
      }),
      user: Object.assign({
      }, appData.user),
      i18n: Object.assign({
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
        title_subject_comments: 'Comments',
        btn_endmeeting: 'End'
      }, appData.i18n)
    }
  },

  methods: {
    execEnd: function () {
      this.app.meet.getRequest({
        url: this.app.meet.getURL('/app/meet/{meet_id}/end', this.app.meet),
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
