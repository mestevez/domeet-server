<template>
  <div id="errapp">
    <v-app>
      <v-content>
        <v-container fluid fill-height>
          <v-layout align-center justify-center>
            <v-flex xs12 sm6 offset-sm3>
              <v-card>
                <v-card-title primary-title class="primary white--text display-3">
                  {{ i18n.label_error }} {{ app.status_code }}
                </v-card-title>
                <v-card-text>
                  {{ app.error_message }}
                </v-card-text>
                <v-card-actions>
                  <v-btn flat color="purple" @click.native="goBack">{{i18n.btn_back}}</v-btn>
                  <v-spacer></v-spacer>
                  <v-btn icon @click.native="showTrace = !showTrace">
                    <v-icon>{{ showTrace ? 'keyboard_arrow_down' : 'keyboard_arrow_up' }}</v-icon>
                  </v-btn>
                </v-card-actions>
                <v-slide-y-transition>
                  <v-card-text v-show="showTrace">
                    {{ app.error_trace }}
                  </v-card-text>
                </v-slide-y-transition>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-content>
    </v-app>
  </div>
</template>

<script>
const errappData = window.errappData || {}
export default {
  name: 'Error',

  data () {
    return {
      showTrace: false,
      app: Object.assign({
        status_code: 404,
        error_message: 'Oops! You have come to the wrong link',
        error_trace: ''
      }, errappData.app),
      i18n: Object.assign({
        btn_back: 'Back',
        label_error: 'Error'
      }, errappData.i18n)
    }
  },

  methods: {
    goBack: function () {
      window.history.back()
    }
  }
}
</script>

<style>
</style>
