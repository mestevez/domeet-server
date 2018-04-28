<template>
  <v-app id="home">
    <apptoolbar></apptoolbar>
    <v-content>
      <v-card>
        <v-card-media src="/static/resources/images/empty.png" height="200px" contain>
        </v-card-media>
        <v-card-title primary-title>
          <div>
            <h3 class="headline mb-0">{{ i18n.lbl_empty_title }}</h3>
            <div>{{ i18n.lbl_empty_message }}</div>
          </div>
        </v-card-title>
      </v-card>
      <v-btn
        color="pink"
        dark
        small
        absolute
        bottom
        right
        fab
        style="bottom: 60px"
      >
        <v-icon>add</v-icon>
      </v-btn>
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
  name: 'Home',
  components: {
    'apptoolbar': AppToolbar
  },

  data () {
    return {
      app: Object.assign({
        meetings: []
      }, appData.app),
      i18n: Object.assign({
        lbl_empty_title: 'No meetings available',
        lbl_empty_message: 'Wait to be invited to a meeting or create your own meeting by the \'Add button\''
      }, appData.i18n)
    }
  }
}
</script>

<style scoped>

</style>
