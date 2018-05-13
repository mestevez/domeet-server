<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_subjects }} ({{ meetData.subjects.length }})</v-flex>
          </v-layout>
        </div>
        <v-list>
          <template v-for="(subject, index) in meetData.subjects.toJSON()">
            <v-divider v-if="index &gt; 0" :key="index" inset></v-divider>
            <v-list-tile :key="subject.subject_id">
              <v-list-tile-content>
                <v-layout>
                  <v-flex>{{ subject.subject_title }}</v-flex>
                </v-layout>
              </v-list-tile-content>
              <v-list-tile-action>
                <v-btn v-if="subject.subject_time_start == null" flat icon color="primary" @click="startSubject(subject)">
                  <v-icon>play_circle_filled</v-icon>
                </v-btn>
                <v-btn v-else-if="subject.subject_time_end == null" flat icon color="warning">
                  <v-icon>timelapse</v-icon>
                </v-btn>
                <v-icon v-else color="success" >done_all</v-icon>
              </v-list-tile-action>
            </v-list-tile>
          </template>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
export default {
  name: 'MeetSubjectsList',
  props: ['meetData', 'i18nData'],
  data () {
    return {
      i18n: Object.assign({
        title_subjects: 'Subjects'
      }, this.i18nData)
    }
  },
  methods: {
    startSubject: function (subject) {
      this.meetData.getRequest({
        url: this.meetData.getURL('/app/meet/subject/{subject_id}/start', subject),
        method: 'POST'
      }).send().then((response) => {
        window.location = window.location
      }).catch((error) => {
        this._evtRequestError(error.response)
      })
    }
  }
}
</script>

<style scoped>

</style>
