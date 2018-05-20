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
            <v-dialog v-model="passwordDialog" persistent>
              <v-btn color="primary" slot="activator" @click="passwordDialog = !passwordDialog">{{ i18n.btn_change_password }}</v-btn>
              <v-card>
                <v-toolbar dark color="primary">
                  <v-tooltip bottom class="ma-0">
                    <v-btn
                      icon
                      large
                      slot="activator"
                      @click="passwordDialog = !passwordDialog"
                    >
                      <v-icon large>keyboard_arrow_left</v-icon>
                    </v-btn>
                    <span>{{ i18n.btn_cancel }}</span>
                  </v-tooltip>
                  <v-toolbar-title>{{ i18n.title_change_password }}</v-toolbar-title>
                </v-toolbar>
                <v-card-title primary-title>
                  <v-container fluid>
                    <v-form method="POST" action="/app/account/password" v-model="validPass" ref="formpass">
                      <v-text-field
                        :label="i18n.label_password_old"
                        :rules="[v => v.length > 0 || i18n.rule_required]"
                        :append-icon="password_old_view ? 'visibility' : 'visibility_off'"
                        :append-icon-cb="() => (password_old_view = !password_old_view)"
                        autofocus
                        :type="password_old_view ? 'password' : 'text'"
                        v-model="password_old_data"
                        name="password_old"
                        required
                      ></v-text-field>
                      <v-text-field
                        :label="i18n.label_password_new"
                        :rules="[v => v.length > 0 || i18n.rule_required]"
                        :append-icon="password_new_view ? 'visibility' : 'visibility_off'"
                        :append-icon-cb="() => (password_new_view = !password_new_view)"
                        :type="password_new_view ? 'password' : 'text'"
                        v-model="password_new_data"
                        name="password_new"
                        required
                      ></v-text-field>
                      <v-text-field
                        :label="i18n.label_password_confirm"
                        :rules="[v => v.length == 0 ? i18n.rule_required : (password_new_data == v || i18n.rule_password_confirm)]"
                        :append-icon="password_confirm_view ? 'visibility' : 'visibility_off'"
                        :append-icon-cb="() => (password_confirm_view = !password_confirm_view)"
                        :type="password_confirm_view ? 'password' : 'text'"
                        v-model="password_confirm_data"
                        required
                      ></v-text-field>
                    </v-form>

                  </v-container>
                </v-card-title>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="error" @click="passwordDialog = !passwordDialog">{{ i18n.btn_cancel }}</v-btn>
                  <v-btn color="success" @click="$refs.formpass.validate() && $refs.formpass.$el.submit()">{{ i18n.btn_ok }}</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
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
              <input type="file" ref="user_photo" :name="photo_changed ? 'user_photo' : 'user_photo_no'" accept="image/*" @change="onFileChange" style="display: none">
              <v-btn color="primary"  @click="$refs.user_photo.click()">{{ i18n.btn_change_photo }}</v-btn>
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
      validPass: true,
      user_photo_src: appData.app.user_photo,
      passwordDialog: false,
      password_old_data: '',
      password_new_data: '',
      password_confirm_data: '',
      password_old_view: true,
      password_new_view: true,
      password_confirm_view: true,
      photo_changed: false,
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
        label_change_password: 'Change password',
        label_password_old: 'Old password',
        label_password_new: 'New password',
        label_password_confirm: 'Confirm password',
        label_phonenum: 'Phone number',
        label_workinghours: 'Working hours',
        head_account_info: 'Account info',
        head_personal_data: 'Personal data',
        head_workinfo: 'Work info',
        rule_invalid_mail: 'E-mail must be valid',
        rule_required: 'This field is required',
        rule_password_confirm: 'Password does not match the confirm password',
        title_change_password: 'Change password'
      }, appData.i18n)
    }
  },

  computed: {
    formEnctype () {
      return this.photo_changed ? 'multipart/form-data' : undefined
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
      me.photo_changed = true
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
      this.photo_changed = true
      this.$refs.form.$el.elements['user_photo'].value = ''
    }
  }
}
</script>

<style scoped>

</style>
