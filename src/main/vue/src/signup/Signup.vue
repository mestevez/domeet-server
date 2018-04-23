<template>
  <div id="signup">
    <v-app>
      <v-content>
        <v-container fluid fill-height>
          <v-layout align-center justify-center>
            <v-flex xs12 sm8 md4>
              <v-card class="elevation-12">
                <v-toolbar dark color="primary">
                  <v-tooltip bottom>
                    <v-btn
                      icon
                      large
                      :href="navigate.login"
                      slot="activator"
                    >
                      <v-icon large>keyboard_arrow_left</v-icon>
                    </v-btn>
                    <span>{{ i18n.label_back }}</span>
                  </v-tooltip>
                  <v-toolbar-title>{{ i18n.title_login }}</v-toolbar-title>
                </v-toolbar>
                <v-card-text>
                  <v-form method="POST" action="/login/signup" v-model="valid" ref="form" lazy-validation>
                    <v-text-field
                      prepend-icon="person"
                      name="email"
                      :label="i18n.label_username"
                      type="text"
                      v-model="username"
                      :rules="emailRules"
                      @keypress.enter.stop="submit"
                      required
                    ></v-text-field>
                    <v-text-field
                      prepend-icon="lock"
                      name="password"
                      :label="i18n.label_password"
                      id="password"
                      type="password"
                      v-model="password"
                      :rules="[() => password.length > 0 || i18n.rule_required]"
                      @keypress.enter.stop="submit"
                      required
                    ></v-text-field>
                    <v-text-field
                      prepend-icon="lock"
                      :label="i18n.label_password_confirm"
                      id="password_confirm"
                      type="password"
                      v-model="password_confirm"
                      :rules="[() => password.length > 0 || i18n.rule_required, () => password == password_confirm || i18n.rule_password_confirm]"
                      @keypress.enter.stop="submit"
                      required
                    ></v-text-field>
                  </v-form>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn type="submit" color="primary" @click="submit">{{ i18n.btn_signup }}</v-btn>
                  <v-spacer></v-spacer>
                </v-card-actions>
                <v-card-text>
                  {{ i18n.label_termsandconds }} <a :href="navigate.terms">{{ i18n.btn_termsandconds }}</a>
                </v-card-text>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-content>
    </v-app>
  </div>
</template>

<script>
const appData = window.appData || {}
export default {
  name: 'Signup',

  data () {
    return {
      username: '',
      password: '',
      password_confirm: '',
      navigate: {
        login: '/login',
        terms: '/login/signup/terms'
      },
      valid: true,
      emailRules: [
        v => {
          return !!v || this.i18n.rule_required
        },
        v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || this.i18n.rule_invalid_mail
      ],
      app: Object.assign({ }, appData.app),
      i18n: Object.assign({
        title_login: 'Sign Up',
        label_back: 'Back',
        label_username: 'Email',
        label_password: 'Password',
        label_password_confirm: 'Confirm password',
        label_termsandconds: 'By signing up you agree to the',
        btn_termsandconds: 'terms and conditions',
        btn_signup: 'Sign up',
        rule_invalid_mail: 'E-mail must be valid',
        rule_required: 'This field is required',
        rule_password_confirm: 'Passwords didn\'t match'
      }, appData.i18n)
    }
  },

  methods: {
    submit () {
      if (this.$refs.form.validate()) {
        this.$refs.form.$el.submit()
      }
    }
  }
}
</script>

<style>
</style>
