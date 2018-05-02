<template>
  <v-card v-if="meetingsList.length &gt; 0" style="max-width: 600px" class="ma-2 pa-2">
    <v-card-title primary-title>
      <div>
        <h3 class="headline mb-0">{{ cardTitle }}</h3>
      </div>
    </v-card-title>
    <v-list two-line>
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
              <v-flex xs12 sm4>{{ parseDate(meetItem.meet_dates[0].meet_date) }}</v-flex>
              <v-flex xs12 sm6><v-list-tile-title v-html="meetItem.meet_title"></v-list-tile-title></v-flex>
              <v-flex xs12 sm2 class="text-sm-right">{{ meetItem.meet_duration }} min</v-flex>
              <v-flex xs12 sm5>{{ meetItem.meet_leader.user_firstname }} {{ meetItem.meet_leader.user_lastname }}</v-flex>
              <v-flex xs12 sm2 class="text-sm-center">
                <v-chip v-if="meetItem.meet_state <= MeetingState.EDIT" label color="primary" text-color="white">{{ i18n.label_meet_state_edit }}</v-chip>
                <v-chip v-else-if="meetItem.meet_state <= MeetingState.READY && isOverdue(meetItem)" label outline color="error">{{ i18n.label_meet_state_overdue }}</v-chip>
                <v-chip v-else-if="meetItem.meet_state <= MeetingState.READY" label>{{ i18n.label_meet_state_ready }}</v-chip>
                <v-chip v-else-if="meetItem.meet_state <= MeetingState.CANCELLED" label outline color="error">{{ i18n.label_meet_state_cancelled }}</v-chip>
                <v-chip v-else-if="meetItem.meet_state <= MeetingState.STARTED" label color="success" text-color="white">{{ i18n.label_meet_state_started }}</v-chip>
                <v-chip v-else label>Ended</v-chip>
              </v-flex>
              <v-flex xs12 sm5 class="text-sm-right">Info</v-flex>
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
        label_meet_state_edit: 'On edit',
        label_meet_state_overdue: 'Overdue',
        label_meet_state_ready: 'Ready',
        label_meet_state_cancelled: 'Cancelled',
        label_meet_state_started: 'Started'
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
