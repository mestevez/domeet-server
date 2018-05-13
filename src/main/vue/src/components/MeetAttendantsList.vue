<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_attendants }} ({{ meetData.attendants.length }})</v-flex>
          </v-layout>
        </div>
        <v-list>
          <v-list-tile v-for="attd in meetData.attendants.toJSON()" :key="attd.user_id.user_id">
            <v-list-tile-avatar>
              <img
                v-if="attd.user_id.user_photo != null"
                :src="attd.user_id.user_photo"
                alt=""
              >
              <v-icon v-else x-large>account_circle</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title>
                <v-layout row nowrap align-center>
                  <v-flex xs2 class="text-xs-right">
                    <v-icon v-if="attd.attd_status == AttendState.PENDING" color="warning">event</v-icon>
                    <v-icon v-else-if="attd.attd_status == AttendState.NOTIFIED" color="primary">event</v-icon>
                    <v-icon v-else-if="attd.attd_status == AttendState.REJECTED" color="error">event_busy</v-icon>
                    <v-icon v-else color="success">event_available</v-icon>
                  </v-flex>
                  <v-flex xs10>{{ attd.user_id.user_firstname }} {{ attd.user_id.user_lastname }}</v-flex>
                </v-layout>
              </v-list-tile-title>
            </v-list-tile-content>
            <v-list-tile-action v-if="isLeader && meetData.meet_state < MeetingState.ENDED" style="justify-content: center">
              <v-tooltip v-if="attd.attd_status < AttendState.CONFIRMED" top>
                <v-btn slot="activator" icon small color="success" @click="confirmAttendant(attd)">
                  <v-icon>person</v-icon>
                </v-btn>
                <span>{{ i18n.btn_join_attendant }}</span>
              </v-tooltip>
              <v-tooltip v-if="attd.attd_status >= AttendState.CONFIRMED" top>
                <v-btn slot="activator" icon small color="error" @click="rejectAttendant(attd)">
                  <v-icon>person_outline</v-icon>
                </v-btn>
                <span>{{ i18n.btn_reject_attendant }}</span>
              </v-tooltip>

            </v-list-tile-action>
          </v-list-tile>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
import { AttendState } from '@/model/attendant'
import { MeetingState } from '@/model/meeting'

export default {
  name: 'MeetAttendantsList',
  props: ['meetData', 'i18nData', 'isLeader'],
  data () {
    return {
      AttendState: AttendState,
      MeetingState: MeetingState,
      i18n: Object.assign({
        btn_join_attendant: 'Add attendant to meeting',
        btn_reject_attendant: 'Remove attendant from meeting',
        title_attendants: 'Attendants'
      }, this.i18nData)
    }
  },
  methods: {
    confirmAttendant: function (attd) {
      this.meetData.getRequest({
        url: this.meetData.getURL('/app/meet/{meet_id}/attend/{user_id}/confirm', {
          meet_id: this.meetData.meet_id,
          user_id: attd.user_id.user_id
        }),
        method: 'POST'
      }).send()
    },
    rejectAttendant: function (attd) {
      this.meetData.getRequest({
        url: this.meetData.getURL('/app/meet/{meet_id}/attend/{user_id}/reject', {
          meet_id: this.meetData.meet_id,
          user_id: attd.user_id.user_id
        }),
        method: 'POST'
      }).send()
    }
  }
}
</script>

<style scoped>

</style>
