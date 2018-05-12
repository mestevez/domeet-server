<template>
  <v-app id="home">
    <apptoolbar></apptoolbar>
    <v-content>
      <v-container fluid fill-height style="max-width: 600px; position: relative;">
        <v-layout column>
          <v-card v-if="app.meetingsonedit.length == 0 && app.meetingsforthcomming.length == 0 && app.meetingsforegoing.length == 0">
            <v-card-media src="/static/resources/images/empty.png" height="200px" contain>
            </v-card-media>
            <v-card-title primary-title>
              <div>
                <h3 class="headline mb-0">{{ i18n.lbl_empty_title }}</h3>
                <div>{{ i18n.lbl_empty_message }}</div>
              </div>
            </v-card-title>
          </v-card>
          <meetingslist
            :meetingsList="app.meetingsonedit"
            :cardTitle="i18n.title_meetingsonedit"
            :navURL="app.navigation.meetedit"
            :dateFormat="user.locale.timestampformat"
            :i18nData="i18n"
          ></meetingslist>
          <meetingslist
            :meetingsList="app.meetingsforthcomming"
            :cardTitle="i18n.title_meetingsforthcomming"
            :navURL="app.navigation.meetedit"
            :dateFormat="user.locale.timestampformat"
            :i18nData="i18n"
          ></meetingslist>
          <meetingslist
            :meetingsList="app.meetingsforegoing"
            :cardTitle="i18n.title_meetingsforegoing"
            :navURL="app.navigation.meetedit"
            :dateFormat="user.locale.timestampformat"
            :i18nData="i18n"
          ></meetingslist>
          <v-btn
            color="pink"
            dark
            absolute
            bottom
            right
            fab
            style="bottom: 60px"
            @click="createNewMeeting"
            fixed
          >
            <v-icon>add</v-icon>
          </v-btn>
        </v-layout>
      </v-container>
    </v-content>
    <appfooter></appfooter>
    <apperrortoast :message="ajaxerror" :i18n="i18n"></apperrortoast>
  </v-app>
</template>

<script>
import '../resources/css/generic.css'
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
import MeetingsList from '@/components/MeetingsList'

const appData = window.appData || {}
export default {
  name: 'Home',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter,
    'meetingslist': MeetingsList
  },
  created: function () {
    this.$initWebsocket('/user/meetings', () => {
      this.$socket.onmessage = (e) => {
        var data = JSON.parse(e.data)
        if (data.message === 'observe' && data.success === true) {
          // DO NOTHING
        } else if (data.message === 'update' && data.key === this.user.user_id) {
          this.refreshMeetings()
        }
      }

      this.$socket.sendObj({ message: 'observe', entity: 'user', key: this.user.user_id })
    })
  },

  data () {
    return {
      app: Object.assign({
        meetingsonedit: [],
        meetingsforthcomming: [],
        meetingsforegoing: [],
        navigation: {}
      }, appData.app),
      user: Object.assign({
        user_photo: null
      }, appData.user),
      i18n: Object.assign({
        lbl_empty_title: 'No meetings available',
        lbl_empty_message: 'Wait to be invited to a meeting or create your own meeting by the \'Add button\'',
        title_meetingsonedit: 'Drafts',
        title_meetingsforthcomming: 'Forthcomming meetings',
        title_meetingsforegoing: 'Foregoing meetings'
      }, appData.i18n)
    }
  },

  methods: {
    createNewMeeting: function () {
      this.$post(this.app.navigation.meetentry)
    },
    refreshMeetings: function () {
      this.$get('/app/meetings').then((response) => {
        this.app.meetingsonedit = response.data.meetingsonedit
        this.app.meetingsforthcomming = response.data.meetingsforthcomming
        this.app.meetingsforegoing = response.data.meetingsforegoing
      })
    }
  }
}
</script>

<style scoped>

</style>
