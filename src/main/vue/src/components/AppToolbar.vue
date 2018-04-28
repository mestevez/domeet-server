<template>
  <div>
    <v-toolbar dark color="primary" fixed app>
      <v-tooltip
        v-if="app.navigation != null && app.navigation.back != null"
        bottom>
        <v-btn
          icon
          large
          slot="activator"
          :href="app.navigation.back"
        >
          <v-icon large>keyboard_arrow_left</v-icon>
        </v-btn>
        <span>{{ i18n.label_back }}</span>
      </v-tooltip>
      <v-toolbar-title>{{ i18n.apptoolbar_title }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-dialog
        max-width="400px"
        hide-overlay
      >
        <v-btn icon flat slot="activator">
          <v-avatar secondary size="40">
            <img
              v-if="user.user_photo != null"
              :src="user.user_photo"
              alt=""
            >
            <v-icon v-else>account_circle</v-icon>
          </v-avatar>
        </v-btn>
        <v-card>
          <v-container fluid grid-list-lg>
            <v-layout row>
              <v-flex xs4>
                <v-container align-center>
                  <v-avatar size="64">
                    <img
                      v-if="user.user_photo != null"
                      :src="user.user_photo"
                      alt=""
                    >
                    <v-icon v-else x-large>account_circle</v-icon>
                  </v-avatar>
                </v-container>
              </v-flex>
              <v-flex xs8>
                <div>
                  <div class="headline">{{ user.user_first_name }} {{ user.user_last_name }}</div>
                  <div>{{ user.user_email }}</div>
                </div>
              </v-flex>
            </v-layout>
          </v-container>
          <v-card-actions>
            <v-btn color="primary" flat :href="navigation.editprofile">{{ i18n.apptoolbar_btn_profile }}</v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary" flat :href="navigation.logout">{{ i18n.apptoolbar_btn_logout }}</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-toolbar>
  </div>
</template>

<script>
const appData = window.appData || {}
export default {
  name: 'AppToolbar',
  data () {
    return {
      navigation: {
        editprofile: '/app/account',
        logout: '/login/logout'
      },
      app: Object.assign({}, appData.app),
      user: Object.assign({
        user_email: 'Undefined',
        user_first_name: 'Undefined',
        user_last_name: 'Undefined',
        user_photo: null
      }, appData.user),
      i18n: Object.assign({
        apptoolbar_title: 'DoMeet',
        apptoolbar_btn_logout: 'Log out',
        apptoolbar_btn_profile: 'Edit Profile'
      }, appData.i18n)
    }
  }
}
</script>

<style scoped>

</style>
