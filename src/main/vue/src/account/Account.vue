<template>
  <v-app id="account">
    <apptoolbar></apptoolbar>
    <v-content>
      <v-form v-model="valid">
        <v-text-field
          :label="i18n.label_email"
          v-model="app.user_email"
          :rules="emailRules"
          required
        ></v-text-field>
        <v-text-field
          :label="i18n.label_first_name"
          v-model="app.user_firstname"
          :rules="[v => v.length > 0 || i18n.rule_required]"
          required
        ></v-text-field>
        <v-text-field
          :label="i18n.label_last_name"
          v-model="app.user_lastname"
          :rules="[v => v.length > 0 || i18n.rule_required]"
          required
        ></v-text-field>
        <v-text-field
          :label="i18n.label_phonenum"
          v-model="app.user_phone"
        ></v-text-field>
        <v-text-field
          :label="i18n.label_company"
          v-model="app.user_company"
        ></v-text-field>
        <v-text-field
          :label="i18n.label_phonenum"
          v-model="app.user_phone"
        ></v-text-field>
        <v-text-field
          :label="i18n.label_workinghours"
          v-model="app.user_schedule"
        ></v-text-field>
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
        user_schedule: ''
      }, appData.app),
      i18n: Object.assign({
        btn_change_photo: 'Change picture',
        btn_change_password: 'Change password',
        label_email: 'Email',
        label_first_name: 'First name',
        label_last_name: 'Last name',
        label_phonenum: 'Phone mumber',
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
