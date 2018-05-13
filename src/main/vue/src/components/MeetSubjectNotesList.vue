<template>
  <v-layout row wrap>
    <v-flex xs12 v-for="noteType in noteTypes" :key="noteType.note_type" v-if="noteType.notes.length &gt; 0">
      <v-card>
        <v-card-title primary-title>
          <v-container flex xs12>
            <div class="headline">{{ noteType.title }} ({{ noteType.notes.length }})</div>
            <v-list>
              <template v-for="(note, inote) in noteType.notes">
                <v-subheader :key="inote" v-if="inote === 0">{{ note.subject }}</v-subheader>
                <v-divider :key="inote*-1" v-else></v-divider>
                <v-list-tile :key="note.note_id">
                  <v-list-tile-content>{{ note.note_description }}</v-list-tile-content>
                </v-list-tile>
              </template>
            </v-list>
          </v-container>
        </v-card-title>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
import { SubjectNoteType } from '@/model/subject_note'

export default {
  name: 'MeetSubjectNotesList',
  props: ['meetData', 'i18nData'],
  computed: {
    noteTypes: function () {
      let decisions = []
      let agreements = []
      let unsettleds = []
      let comments = []
      this.meetData.subjects.each((subject) => {
        subject.notes.forEach((note) => {
          note.subject = subject.subject_title
          switch (note.note_type) {
            case SubjectNoteType.DECISION:
              decisions.push(note)
              break
            case SubjectNoteType.AGREEMENT:
              agreements.push(note)
              break
            case SubjectNoteType.UNSETTLED:
              unsettleds.push(note)
              break
            case SubjectNoteType.COMMENT:
              comments.push(note)
              break
          }
        })
      })
      return [
        {
          note_type: SubjectNoteType.DECISION,
          title: this.i18n.title_subject_decisions,
          notes: decisions
        },
        {
          note_type: SubjectNoteType.AGREEMENT,
          title: this.i18n.title_subject_agreements,
          notes: agreements
        },
        {
          note_type: SubjectNoteType.UNSETTLED,
          title: this.i18n.title_subject_unsettled,
          notes: unsettleds
        },
        {
          note_type: SubjectNoteType.COMMENT,
          title: this.i18n.title_subject_comments,
          notes: comments
        }
      ]
    }
  },
  data () {
    return {
      SubjectNoteType: SubjectNoteType,
      i18n: Object.assign({
        title_subject_decisions: 'Decisions Taken',
        title_subject_agreements: 'Agreements',
        title_subject_unsettled: 'Pending issues',
        title_subject_comments: 'Comments'
      }, this.i18nData)
    }
  }
}
</script>

<style scoped>

</style>
