<template>
  <v-app id="account">
    <apptoolbar></apptoolbar>
    <v-content style="max-width: 600px" class="px-2">
      <v-form method="POST" v-model="valid" ref="form" :enctype="formEnctype">
        <v-subheader>{{ i18n.head_account_info }}</v-subheader>
        <v-layout row wrap>
          <v-flex sm7 xs12>
            <v-text-field
              :label="i18n.label_email"
              v-model="app.user_email"
              :rules="emailRules"
              required
              :readonly="true"
              style="min-height: 96px;"
            ></v-text-field>
            <v-btn color="primary">{{ i18n.btn_change_password }}</v-btn>
          </v-flex>
          <v-flex sm5 xs12 class="text-xs-left text-sm-center">
            <v-avatar size="96">
              <img
                v-if="app.user_photo != null"
                :src="user_photo_src"
                alt=""
              >
              <v-icon v-else size="96">account_circle</v-icon>
            </v-avatar>
            <div>
              <input type="file" name="user_photo" accept="image/*" @change="onFileChange" style="display: none">
              <v-btn color="primary"  @click="$refs.form.$el.elements['user_photo'].click()">{{ i18n.btn_change_photo }}</v-btn>
              <v-tooltip bottom lazy>
                <v-btn v-if="app.user_photo != null" slot="activator" fab small dark color="error" @click="removeFile">
                  <v-icon dark>delete_forever</v-icon>
                </v-btn>
                <span>{{ i18n.btn_remove_photo }}</span>
              </v-tooltip>
            </div>
          </v-flex>
        </v-layout>
        <v-subheader>{{ i18n.head_personal_data }}</v-subheader>
        <v-text-field
          :label="i18n.label_first_name"
          v-model="app.user_firstname"
          name="user_firstname"
          :rules="[v => v.length > 0 || i18n.rule_required]"
          required
        ></v-text-field>
        <v-text-field
          :label="i18n.label_last_name"
          v-model="app.user_lastname"
          name="user_lastname"
        ></v-text-field>
        <v-text-field
          :label="i18n.label_phonenum"
          v-model="app.user_phone"
          name="user_phone"
        ></v-text-field>
        <v-subheader>{{ i18n.head_workinfo }}</v-subheader>
        <v-text-field
          :label="i18n.label_company"
          v-model="app.user_company"
          name="user_company"
        ></v-text-field>
        <v-select
          :items="app.working_hours"
          :label="i18n.label_workinghours"
          v-model="app.user_schedule"
        ></v-select>
        <input type="hidden" name="user_schedule" :value="app.user_schedule">
      </v-form>
    </v-content>
    <appfooter>
      <template slot="actions">
        <v-btn color="success" @click="submit">{{ i18n.btn_save }}</v-btn>
      </template>
    </appfooter>
  </v-app>
</template>

<script>
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
const appData = window.appData || {}
export default {
  name: 'Account',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter
  },

  data () {
    return {
      valid: true,
      user_photo_src: appData.app.user_photo,
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
        btn_change_password: 'Change password',
        btn_change_photo: 'Change picture',
        btn_remove_photo: 'Remove picture',
        btn_save: 'Save',
        label_company: 'Company',
        label_email: 'Email',
        label_first_name: 'First name',
        label_last_name: 'Last name',
        label_phonenum: 'Phone number',
        label_workinghours: 'Working hours',
        head_account_info: 'Account info',
        head_personal_data: 'Personal data',
        head_workinfo: 'Work info',
        rule_invalid_mail: 'E-mail must be valid',
        rule_required: 'This field is required'
      }, appData.i18n)
    }
  },

  computed: {
    formEnctype () {
      return this.app.user_photo == null ? undefined : 'multipart/form-data'
    }
  },

  methods: {
    submit () {
      if (this.$refs.form.validate()) {
        this.$refs.form.$el.submit()
      }
    },
    onFileChange (file) {
      let me = this
      me.app.user_photo = file

      if (typeof photosrc !== 'string') {
        var reader = new FileReader()
        reader.readAsDataURL(file.target.files[0])
        reader.onload = function () {
          me.user_photo_src = reader.result
        }
      }
    },
    removeFile () {
      this.app.user_photo = null
      this.$refs.form.$el.elements['user_photo'].value = ''
    }
  }
}
</script>

<style scoped>

</style>
