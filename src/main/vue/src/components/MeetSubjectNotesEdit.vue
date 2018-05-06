<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ title }} ({{ notes.length }})</v-flex>
            <v-spacer></v-spacer>
            <v-flex class="text-xs-right"><v-btn color="primary" @click="createNote">{{  addLabel }}</v-btn></v-flex>
          </v-layout>
        </div>
        <div v-if="notes.length == 0" v-html="nodata"></div>
        <v-list v-else three-line>
          <template v-for="(note, index) in notes">
            <v-divider v-if="index &gt; 0" :key="index"></v-divider>
            <v-list-tile :key="note.note_id">
              <v-list-tile-action class="align-start">
                <v-btn flat icon color="primary" @click="deleteNote(note)">
                  <v-icon>delete</v-icon>
                </v-btn>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-text-field
                  v-model="notes[index].note_description"
                  rows="3"
                  multi-line
                  single-line
                  full-width
                  @input="updateNote(notes[index])"
                  class="pa-0"
                ></v-text-field>
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
  props: ['subject', 'noteType', 'title', 'addLabel', 'nodata'],
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
      onUpdate: false,
      i18n: Object.assign({
      }, this.i18nData)
    }
  },
  methods: {
    createNote: function () {
      this.onUpdate = true
      this.subject.getRequest({
        url: this.subject.getURL('/app/meet/subject/{subject_id}/note', this.subject),
        method: 'POST',
        params: {
          note_type: this.noteType
        }
      }).send().then((response) => {
        this.onUpdate = false
        window.location = window.location
        // this.meetData.subjects.fetch().then((response) => { })
      }).catch(() => {
        this.onUpdate = false
      })
    },
    deleteNote: function (note) {
      note.delete().then((response) => {
        window.location = window.location
      })
    },
    updateNote: function (note) {
      if (this.updateTimeout != null) {
        clearTimeout(this.updateTimeout)
      }

      this.updateTimeout = setTimeout(() => {
        this.updateTimeout = null
        this.onUpdate = true
        note.save().then(() => {
          this.onUpdate = false
        }).catch(() => {
          this.onUpdate = false
        })
      }, 500)
    }
  }
}
</script>

<style scoped>

</style>
