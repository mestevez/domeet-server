<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_subjects }} ({{ meetData.subjects.length }})</v-flex>
            <v-spacer></v-spacer>
            <v-flex class="text-xs-right"><v-btn color="primary" @click="createSubject">{{  i18n.btn_addsubject}}</v-btn></v-flex>
          </v-layout>
        </div>
        <div v-if="meetData.subjects.length == 0" v-html="i18n.label_nosubjects"></div>
        <v-list v-else>
          <template v-for="(subject, index) in meetData.subjects">
            <v-divider v-if="index &gt; 0" :key="index" inset></v-divider>
            <v-list-tile avatar :key="subject.title">
              <v-list-tile-action>
                <v-btn flat icon color="primary" @click="deleteSubject(subject)">
                  <v-icon>delete</v-icon>
                </v-btn>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title v-html="subject.title"></v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </template>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
export default {
  name: 'MeetSubjectsEdit',
  props: ['meetData', 'i18nData'],
  data () {
    return {
      i18n: Object.assign({
        title_subjects: 'Subjects',
        label_nosubjects: 'It looks like you have not defined any subject yet. Add one or more subjects of the meeting.',
        btn_addsubject: 'Add subject',
        btn_delsubject: 'Remove subject'
      }, this.i18nData)
    }
  },
  methods: {
    createSubject: function () {
      this.meetData.getRequest({
        url: this.meetData.getURL('/app/meet/{meet_id}/subject/entry', this.meetData),
        method: 'POST'
      }).send().then((response) => {
        if (response.response.data.redirect) {

        }
      }).catch((error) => {
        this._evtRequestError(error.response)
      })
    },
    deleteSubject: function (subject) {
      this.meetData.getRequest({
        url: this.meetData.getURL('/app/meet/subject/{subject_id}', subject),
        method: 'DELETE'
      }).send().then((response) => {
        console.log('DELETED')
      })
    }
  }
}
</script>

<style scoped>

</style>
