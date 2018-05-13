<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout row nowrap align-center>
            <v-flex xs6>{{ i18n.title_attendants }} ({{ meetData.attendants.length }})</v-flex>
            <v-flex xs6 class="text-xs-right align-center">
              <v-menu v-if="isLeader" v-model="showMenu" absolute top offset-y>
                <v-text-field
                  slot="activator"
                  prepend-icon="search"
                  hide-details
                  single-line
                  v-model="searchQuery"
                ></v-text-field>
                <v-list>
                  <v-list-tile v-for="user in usersList" :key="user.user_id" @click="addAttendant(user)">
                    <v-list-tile-title>{{ user.user_firstname }} {{ user.user_lastname }}</v-list-tile-title>
                  </v-list-tile>
                </v-list>
              </v-menu>
            </v-flex>
          </v-layout>
        </div>
        <div v-if="meetData.attendants.length == 0" v-html="i18n.label_noattendants"></div>
        <v-list v-else>
          <template v-for="(attd, index) in meetData.attendants.toJSON()">
            <v-divider v-if="index &gt; 0" :key="index"></v-divider>
            <v-list-tile :key="attd.user_id.user_id">
              <v-list-tile-action v-if="isLeader">
                <v-btn flat icon color="primary" @click="deleteAttendant(attd.user_id)">
                  <v-icon>delete</v-icon>
                </v-btn>
              </v-list-tile-action>
              <v-list-tile-title>
                <v-layout row nowrap align-center>
                  <v-flex xs10>{{ attd.user_id.user_firstname }} {{ attd.user_id.user_lastname }}</v-flex>
                  <v-flex xs2 class="text-xs-right">
                    <v-icon v-if="attd.attd_status == AttendState.PENDING" color="warning">event</v-icon>
                    <v-icon v-if="attd.attd_status == AttendState.NOTIFIED" color="primary">event</v-icon>
                    <v-icon v-if="attd.attd_status == AttendState.REJECTED" color="error">event_busy</v-icon>
                    <v-icon v-if="attd.attd_status == AttendState.CONFIRMED" color="success">event_available</v-icon>
                  </v-flex>
                </v-layout>
              </v-list-tile-title>
            </v-list-tile>
          </template>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
import { AttendState } from '@/model/attendant'

export default {
  name: 'MeetAttendantsEdit',
  props: ['meetData', 'i18nData', 'isLeader'],
  data () {
    return {
      AttendState: AttendState,
      showMenu: false,
      searchQuery: '',
      searchTimeout: null,
      usersList: [],
      i18n: Object.assign({
        label_noattendants: 'It looks like you have not invited anyone yet. Add some people by search them in the field above.',
        title_attendants: 'Attendants'
      }, this.i18nData)
    }
  },

  watch: {
    searchQuery: function () {
      this.showMenu = this.searchQuery.length > 0
      this.searchUser()
    }
  },

  methods: {
    addAttendant: function (user) {
      this.meetData.getRequest({
        url: this.meetData.getURL('/app/meet/{meet_id}/attend/{user_id}', { meet_id: this.meetData.meet_id, user_id: user.user_id }),
        method: 'POST'
      }).send().then(() => {
        this.searchQuery = ''
      })
    },
    deleteAttendant: function (user) {
      this.meetData.getRequest({
        url: this.meetData.getURL('/app/meet/{meet_id}/attend/{user_id}', { meet_id: this.meetData.meet_id, user_id: user.user_id }),
        method: 'DELETE'
      }).send()
    },
    searchUser: function () {
      if (this.searchTimeout != null) {
        clearTimeout(this.searchTimeout)
      }

      if (this.searchQuery.length === 0) {
        this.usersList = []
      } else {
        this.searchTimeout = setTimeout(() => {
          this.searchTimeout = null

          if (this.onSearch) {
            this.searchTimeout = setTimeout(() => {
              this.searchTimeout = null
              this.searchUser()
            })
          } else {
            this.onSearch = true
            this.meetData.getRequest({
              url: this.meetData.getURL('/app/meet/{meet_id}/searchAttendants', this.meetData),
              method: 'GET',
              params: {
                q: this.searchQuery
              }
            }).send().then((response) => {
              this.usersList = response.response.data
              this.onSearch = false
            }).catch(() => {
              this.onSearch = false
            })
          }
        }, 400)
      }
    }
  }
}
</script>

<style scoped>

</style>
