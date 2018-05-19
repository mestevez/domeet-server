<template>
  <v-card>
    <v-card-title primary-title>
      <div class="headline">{{ i18n.title_files }} ({{ meetData.documents.length }})</div>
    </v-card-title>
    <v-card-text>
      <v-list>
        <template v-for="(item, index) in meet_files">
          <v-divider v-if="index > 0" inset :key="index"></v-divider>
          <v-list-tile :key="item.doc_id">
            <v-list-tile-avatar>
              <v-icon v-if="(/text/).test(item.doc_mimetype)">description</v-icon>
              <v-icon v-else-if="(/word/).test(item.doc_mimetype)" color="blue">description</v-icon>
              <v-icon v-else-if="(/sheet/).test(item.doc_mimetype)" color="green">list_alt</v-icon>
              <v-icon v-else-if="(/presentation/).test(item.doc_mimetype)">picture_as_pdf</v-icon>
              <v-icon v-else-if="(/pdf/).test(item.doc_mimetype)" color="red">picture_as_pdf</v-icon>
              <v-icon v-else-if="(/image/).test(item.doc_mimetype)">image</v-icon>
              <v-icon v-else-if="(/video/).test(item.doc_mimetype)">theaters</v-icon>
              <v-icon v-else-if="(/audio/).test(item.doc_mimetype)">audiotrack</v-icon>
              <v-icon v-else>attachment</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <a d-block :href="item.url">{{ item.doc_name }}</a>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  name: 'MeetFilesList',
  props: ['meetData', 'i18nData'],
  computed: {
    meet_files: function () {
      return this.meetData.documents.map((item) => {
        let convItem = item
        convItem.url = this.meetData.getURL('/app/meet/{meet_id}/meet_doc/{doc_id}', {
          meet_id: this.meetData.meet_id,
          doc_id: item.doc_id
        })
        return convItem
      })
    }
  },
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
