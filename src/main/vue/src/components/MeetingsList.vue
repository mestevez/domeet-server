<template>
  <v-card v-if="meetingsList.length &gt; 0" class="ma-2 pa-2">
    <v-card-title primary-title>
      <div>
        <h3 class="headline mb-0">{{ cardTitle }}</h3>
      </div>
    </v-card-title>
    <v-list class="fit-content">
      <template v-for="(meetItem, index) in meetingsList">
        <v-divider v-if="index &gt; 0" inset :key="index"></v-divider>
        <v-list-tile avatar :key="meetItem.meet_id" :href="navURL.replace(/\{([^}]+)\}/g, meetItem.meet_id)">
          <v-list-tile-avatar>
            <img
              v-if="meetItem.meet_leader.user_photo != null"
              :src="meetItem.meet_leader.user_photo"
              alt=""
            >
            <v-icon v-else x-large>account_circle</v-icon>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-layout row wrap style="width: 100%">
              <v-flex xs12>
                <v-list-tile-title class="text-sm-center ellipsis no-wrap">
                  <h3 class="headline mb-0">{{ meetItem.meet_title }}</h3>
                </v-list-tile-title>
              </v-flex>
              <v-flex xs12 sm6>{{ parseDate(meetItem.meet_dates[0].meet_date) }}</v-flex>
              <v-flex xs12 sm6 class="text-sm-right">{{ meetItem.meet_duration }} min</v-flex>
              <v-flex xs12 sm6 class="ellipsis no-wrap">{{ meetItem.meet_leader.user_firstname }} {{ meetItem.meet_leader.user_lastname }}</v-flex>
              <v-flex xs12 sm6 class="text-sm-right ellipsis no-wrap"
                >({{ meetItem.attendants.length }}) {{ i18n.label_attendants }}, ({{ meetItem.subjects.length }}) {{ i18n.label_subjects }}</v-flex>
              <v-flex xs12 class="text-sm-right">
                <v-chip v-if="meetItem.meet_state <= MeetingState.EDIT" label class="mr-0" color="primary" text-color="white">{{ i18n.label_meet_state_edit }}</v-chip>
                <v-chip v-else-if="meetItem.meet_state <= MeetingState.READY && isOverdue(meetItem)" label class="mr-0" outline color="error">{{ i18n.label_meet_state_overdue }}</v-chip>
                <v-chip v-else-if="meetItem.meet_state <= MeetingState.READY" label class="mr-0">{{ i18n.label_meet_state_ready }}</v-chip>
                <v-chip v-else-if="meetItem.meet_state <= MeetingState.CANCELLED" label class="mr-0" outline color="error">{{ i18n.label_meet_state_cancelled }}</v-chip>
                <v-chip v-else-if="meetItem.meet_state <= MeetingState.STARTED" label class="mr-0" color="success" text-color="white">{{ i18n.label_meet_state_started }}</v-chip>
                <v-chip v-else label class="mr-0">Ended</v-chip>
              </v-flex>
            </v-layout>
          </v-list-tile-content>
        </v-list-tile>
      </template>
    </v-list>
  </v-card>
</template>

<script>
import moment from 'moment'
import { MeetingState } from '@/model/meeting'

export default {
  name: 'MeetingsList',
  props: ['meetingsList', 'cardTitle', 'navURL', 'dateFormat', 'i18nData'],
  data () {
    return {
      MeetingState: MeetingState,
      i18n: Object.assign({
        label_attendants: 'Attendants',
        label_meet_state_edit: 'On edit',
        label_meet_state_overdue: 'Overdue',
        label_meet_state_ready: 'Ready',
        label_meet_state_cancelled: 'Cancelled',
        label_meet_state_started: 'Started',
        label_subjects: 'Subjects'
      }, this.i18nData)
    }
  },
  methods: {
    isOverdue: function (meetItem) {
      return new Date(meetItem.meet_dates[0].meet_date) < new Date()
    },
    parseDate: function (isodate) {
      return moment(isodate).format(this.dateFormat)
    }
  }
}
</script>

<style scoped>

</style>
