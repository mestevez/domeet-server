<template>
  <div id="login">
    <v-app id="inspire">
      <v-content>
        <v-container fluid fill-height>
          <v-layout align-center justify-center>
            <v-flex xs12 sm8 md4>
              <v-card class="elevation-12">
                <v-toolbar dark color="primary">
                  <v-toolbar-title>{{ i18n.title_login }}</v-toolbar-title>
                </v-toolbar>
                <v-card-text>
                  <v-form method="POST" action="j_security_check" v-model="valid" ref="form" lazy-validation>
                    <v-text-field
                      prepend-icon="person"
                      name="j_username"
                      :label="i18n.label_username"
                      type="text"
                      v-model="username"
                      :rules="[() => username.length > 0 || i18n.rule_required]"
                      required
                    ></v-text-field>
                    <v-text-field
                      prepend-icon="lock"
                      name="j_password"
                      :label="i18n.label_password"
                      id="password"
                      type="password"
                      v-model="password"
                      :rules="[() => password.length > 0 || i18n.rule_required]"
                      required
                    ></v-text-field>
                  </v-form>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn type="submit" color="primary" @click="submit">{{ i18n.btn_login }}</v-btn>
                  <v-spacer></v-spacer>
                </v-card-actions>
                <v-card-text>
                  {{ i18n.label_register }}
                  <a href="/login/signup">{{ i18n.btn_signup }}</a>
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
const loginappData = window.loginappData || {}
export default {
  name: 'Login',

  data () {
    return {
      username: '',
      password: '',
      valid: true,
      app: Object.assign({ }, loginappData.app),
      i18n: Object.assign({
        title_login: 'Login form',
        label_username: 'Email',
        label_password: 'Password',
        label_register: 'New user?',
        btn_login: 'Login',
        btn_signup: 'Sign up',
        rule_required: 'This field is required'
      }, loginappData.i18n)
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
