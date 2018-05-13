<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ title }} ({{ notes.length }})</v-flex>
            <v-spacer></v-spacer>
            <v-flex class="text-xs-right">
              <v-btn v-if="isLeader" color="primary" @click="createNote">{{ addLabel }}</v-btn>
            </v-flex>
          </v-layout>
        </div>
        <div v-if="notes.length == 0" v-html="nodata"></div>
        <v-list v-else :three-line="isLeader">
          <template v-for="(note, index) in notes">
            <v-divider v-if="index &gt; 0" :key="index"></v-divider>
            <v-list-tile :key="note.note_id">
              <v-list-tile-action v-if="isLeader">
                <v-btn flat icon color="primary" @click="deleteNote(note)">
                  <v-icon>delete</v-icon>
                </v-btn>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-text-field
                  v-if="isLeader"
                  v-model="note.note_description"
                  rows="3"
                  multi-line
                  single-line
                  full-width
                  @input="updateNote(note)"
                  class="pa-0"
                ></v-text-field>
                <v-list-tile-title v-else>{{ note.note_description }}</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </template>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
export default {
  name: 'MeetSubjectNotesEdit',
  props: ['subject', 'noteType', 'title', 'addLabel', 'nodata', 'isLeader'],
  computed: {
    notes: function () {
      let typenotes = []
      this.subject.notes.forEach((note) => {
        if (note.note_type === this.noteType) {
          typenotes.push(note)
        }
      })
      return typenotes
    }
  },
  data () {
    return {
      updateTimeout: null,
      i18n: Object.assign({
      }, this.i18nData)
    }
  },
  methods: {
    createNote: function () {
      this.subject.getRequest({
        url: this.subject.getURL('/app/meet/subject/{subject_id}/note', this.subject),
        method: 'POST',
        params: {
          note_type: this.noteType
        }
      }).send()
    },
    deleteNote: function (note) {
      note.delete()
    },
    updateNote: function (note) {
      if (this.updateTimeout != null) {
        clearTimeout(this.updateTimeout)
      }

      this.updateTimeout = setTimeout(() => {
        this.updateTimeout = null
        note.save()
      }, 500)
    }
  }
}
</script>

<style scoped>

</style>
