<template>
  <v-app id="account">
    <apptoolbar></apptoolbar>
    <v-content style="max-width: 600px" class="px-2">
      <v-form v-model="valid">
        <v-subheader>{{ i18n.head_account_info }}</v-subheader>
        <v-layout row wrap>
          <v-flex sm8 xs12>
            <v-text-field
              :label="i18n.label_email"
              v-model="app.user_email"
              :rules="emailRules"
              required
              style="min-height: 96px;"
            ></v-text-field>
            <v-btn color="primary">{{ i18n.btn_change_password }}</v-btn>
          </v-flex>
          <v-flex sm4 xs12 class="text-xs-left text-sm-center">
            <v-avatar size="96">
              <v-card-media
                v-if="app.user_photo != null"
                src="https://vuetifyjs.com/static/doc-images/cards/foster.jpg"
                height="96px"
                contain
              ></v-card-media>
              <v-icon v-else size="96px">account_circle</v-icon>
            </v-avatar>
            <div><v-btn color="primary">{{ i18n.btn_change_photo }}</v-btn></div>
          </v-flex>
        </v-layout>
        <v-subheader>{{ i18n.head_personal_data }}</v-subheader>
        <v-text-field
          :label="i18n.label_first_name"
          v-model="app.user_firstname"
          :rules="[v => v.length > 0 || i18n.rule_required]"
          required
        ></v-text-field>
        <v-text-field
          :label="i18n.label_last_name"
          v-model="app.user_lastname"
        ></v-text-field>
        <v-text-field
          :label="i18n.label_phonenum"
          v-model="app.user_phone"
        ></v-text-field>
        <v-subheader>{{ i18n.head_workinfo }}</v-subheader>
        <v-text-field
          :label="i18n.label_company"
          v-model="app.user_company"
        ></v-text-field>
        <v-select
          :items="app.working_hours"
          :label="i18n.label_workinghours"
          v-model="app.user_schedule"
        ></v-select>
        <v-btn color="success">{{ i18n.btn_save }}</v-btn>
      </v-form>
    </v-content>
    <v-footer dark color="primary" app>
      <span>&copy; 2018</span>
    </v-footer>
  </v-app>
</template>

<script>
import AppToolbar from '@/components/AppToolbar'
const appData = window.appData || {}
export default {
  name: 'Account',
  components: {
    'apptoolbar': AppToolbar
  },

  data () {
    return {
      valid: true,
      emailRules: [
        v => {
          return !!v || this.i18n.rule_required
        },
        v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || this.i18n.rule_invalid_mail
      ],
      app: Object.assign({
        user_email: '',
        user_firstname: '',
        user_lastname: '',
        user_company: '',
        user_phone: '',
        user_photo: null,
        user_schedule: '',
        working_hours: []
      }, appData.app),
      i18n: Object.assign({
        btn_change_photo: 'Change picture',
        btn_change_password: 'Change password',
        btn_save: 'Save',
        label_email: 'Email',
        label_first_name: 'First name',
        label_last_name: 'Last name',
        label_phonenum: 'Phone number',
        label_company: 'Company',
        label_workinghours: 'Working hours',
        head_account_info: 'Account info',
        head_personal_data: 'Personal data',
        head_workinfo: 'Work info',
        rule_invalid_mail: 'E-mail must be valid',
        rule_required: 'This field is required'
      }, appData.i18n)
    }
  }
}
</script>

<style scoped>

</style>
