<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_subjects }} ({{ app.subjects.length }})</v-flex>
            <v-spacer></v-spacer>
            <v-flex class="text-xs-right"><v-btn color="primary">{{  i18n.btn_addsubject}}</v-btn></v-flex>
          </v-layout>
        </div>
        <div v-if="app.subjects.length == 0" v-html="i18n.label_nosubjects"></div>
        <v-list v-else>
          <template v-for="(subject, index) in app.subjects">
            <v-divider v-if="index &gt; 0" :key="index" inset></v-divider>
            <v-list-tile avatar :key="subject.title">
              <v-list-tile-action>
                <v-icon color="primary">delete</v-icon>
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
const appData = window.appData || {}
export default {
  name: 'MeetSubjectsEdit',
  data () {
    return {
      app: Object.assign({
        subjects: []
      }, appData.app),
      user: Object.assign({
      }, appData.user),
      i18n: Object.assign({
        title_subjects: 'Subjects',
        label_nosubjects: 'It looks like you have not defined any subject yet. Add one or more subjects of the meeting.',
        btn_addsubject: 'Add subject',
        btn_delsubject: 'Remove subject'
      }, appData.i18n)
    }
  }
}
</script>

<style scoped>

</style>
