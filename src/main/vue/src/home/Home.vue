<template>
  <v-app id="home">
    <apptoolbar></apptoolbar>
    <v-content>
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
      <form method="post" :action="app.navigation.meetentry">
        <v-btn
          color="pink"
          dark
          small
          absolute
          bottom
          right
          fab
          style="bottom: 60px"
          type="submit"
        >
          <v-icon>add</v-icon>
        </v-btn>
      </form>
    </v-content>
    <appfooter></appfooter>
  </v-app>
</template>

<script>
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
  }
}
</script>

<style scoped>

</style>
