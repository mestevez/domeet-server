<template>
  <v-navigation-drawer
    fixed
    v-model="notificationsPanel"
    right
    clipped
    app
    permanent
    width="150"
  >
    <v-container
      fluid
      style="min-height: 0;"
      grid-list-lg
    >
      <v-layout row wrap>
        <v-flex v-for="(meet_issue) in meetIssues.toJSON()" :key="meet_issue.issue_code" xs12>
          <v-card :color="issueColor(meet_issue)" class="white--text">
            <v-card-title primary-title>{{ meet_issue.issue_message }}</v-card-title>
            <!--<v-card-actions>-->
            <!--<v-btn flat dark>Listen now</v-btn>-->
            <!--</v-card-actions>-->
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
  </v-navigation-drawer>
</template>

<script>
import { MeetingIssueType } from '@/model/meet_issue'

export default {
  name: 'AppMeetIssues',
  props: ['meetIssues', 'i18n'],

  methods: {
    issueColor (issue) {
      switch (issue.issue_type) {
        case MeetingIssueType.ERROR:
          return 'error'
        case MeetingIssueType.INFO:
          return 'info'
        case MeetingIssueType.WARNING:
          return 'warning'
        default:
          return 'blue-grey darken-2'
      }
    }
  },

  data () {
    return {
      notificationsPanel: true
    }
  }
}
</script>

<style scoped>

</style>
