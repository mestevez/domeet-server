<template>
  <div id="terms">
    <v-app>
      <!--<v-content>-->
        <!--<v-container fluid fill-height>-->
          <v-layout align-center justify-center class="py-2">
            <v-flex xs12 sm8 md8>
              <v-card class="elevation-12">
                <v-toolbar dark color="primary">
                  <v-tooltip bottom>
                    <v-btn
                      icon
                      large
                      :href="navigate.submit"
                      slot="activator"
                    >
                      <v-icon large>keyboard_arrow_left</v-icon>
                    </v-btn>
                    <span>{{ i18n.label_back }}</span>
                  </v-tooltip>
                  <v-toolbar-title>{{ i18n.title_terms }}</v-toolbar-title>
                </v-toolbar>
                <v-card-text>
                  <div class="display-1">{{ i18n.introduction_head }}</div>
                  <p>{{ i18n.introduction_body }}</p>
                  <div class="display-1">{{ i18n.conditions_head }}</div>
                  <p>{{ i18n.conditions_body }}</p>
                  <div class="display-1">{{ i18n.modifications_head }}</div>
                  <p>{{ i18n.modifications_body }}</p>
                  <div class="display-1">{{ i18n.userduties_head }}</div>
                  <p>{{ i18n.userduties_body }}</p>
                  <div class="display-1">{{ i18n.webresponsabilities_head }}</div>
                  <p>{{ i18n.webresponsabilities_body }}</p>
                  <div class="display-1">{{ i18n.copyright_head }}</div>
                  <p>{{ i18n.copyright_body }}</p>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn type="submit" color="primary" :href="navigate.submit">{{ i18n.btn_readit }}</v-btn>
                  <v-spacer></v-spacer>
                </v-card-actions>
              </v-card>
            </v-flex>
          </v-layout>
        <!--</v-container>-->
      <!--</v-content>-->
    </v-app>
  </div>
</template>

<script>
const termsappData = window.termsappData || {}
export default {
  name: 'Terms',

  data () {
    return {
      navigate: {
        submit: '/login/signup'
      },
      app: Object.assign({ }, termsappData.app),
      i18n: Object.assign({
        btn_readit: 'I have read it',
        label_back: 'Back',
        title_terms: 'Terms and conditions'
      }, termsappData.i18n)
    }
  }
}
</script>

<style>
</style>
