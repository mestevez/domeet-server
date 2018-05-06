<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_files }} (0)</v-flex>
          </v-layout>
        </div>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
export default {
  name: 'MeetFilesList',
  props: ['meetData', 'i18nData'],
  data () {
    return {
      i18n: Object.assign({
        title_files: 'Files'
      }, this.i18nData)
    }
  }
}
</script>

<style scoped>

</style>
