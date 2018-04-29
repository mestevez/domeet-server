<template>
  <v-app id="meetentry">
    <v-navigation-drawer
      fixed
      v-model="notificationsPanel"
      right
      clipped
      app
      permanent
      width="150"
    >
    </v-navigation-drawer>
    <apptoolbar></apptoolbar>
    <v-content>
      <v-form v-model="validForm">
        <v-container fluid grid-list-lg>
          <v-layout row wrap>
            <v-flex xs12>
              <generaledit></generaledit>
            </v-flex>
            <v-flex xs12>
              <v-card>
                <v-card-title primary-title>
                  <v-container flex xs12>
                    <div class="headline">{{ i18n.title_subjects }}</div>
                  </v-container>
                </v-card-title>
              </v-card>
            </v-flex>
            <v-flex xs12>
              <v-card>
                <v-card-title primary-title>
                  <v-container flex xs12>
                    <div class="headline">{{ i18n.title_attendants }}</div>
                  </v-container>
                </v-card-title>
              </v-card>
            </v-flex>
            <v-flex xs12>
              <v-card>
                <v-card-title primary-title>
                  <v-container flex xs12>
                    <div class="headline">{{ i18n.title_files }}</div>
                  </v-container>
                </v-card-title>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-form>
    </v-content>
    <v-footer dark color="primary" app>
      <span>&copy; 2018</span>
    </v-footer>
  </v-app>
</template>

<script>
import AppToolbar from '@/components/AppToolbar'
import MeetGeneralEdit from '@/components/MeetGeneralEdit'
const appData = window.appData || {}
export default {
  name: 'MeetEntry',
  components: {
    'apptoolbar': AppToolbar,
    'generaledit': MeetGeneralEdit
  },

  data () {
    return {

      notificationsPanel: true,
      validForm: true,
      app: Object.assign({
      }, appData.app),
      user: Object.assign({

      }, appData.user),
      i18n: Object.assign({

        title_subjects: 'Subjects',
        title_attendants: 'Attendants',
        title_files: 'Files',
        label_search_user: 'Search user',
        btn_addsubject: 'Add subject',
        btn_addfile: 'Add file',
        btn_delfile: 'Remove file',
        btn_delsubject: 'Remove subject',
        btn_delattendant: 'Remove attendant'
      }, appData.i18n)
    }
  }
}
</script>

<style scoped>

</style>
