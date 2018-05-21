<template>
  <v-layout row wrap>
    <v-flex xs12 v-for="(subject, index) in subjectsList" :key="subject.subject_id" v-if="subject.notes.length &gt; 0">
      <v-card>
        <v-card-title primary-title class="headline">
          {{ index + 1}}. {{ subject.subject_title }}
        </v-card-title>
        <v-card-text>
          <v-list class="fit-content">
            <template v-for="(note_type, itype) in subject.notes">
              <v-subheader :key="itype">{{ getNoteTitle(note_type.note_type) }}</v-subheader>
              <template v-for="(note, inote) in note_type.notes">
                <v-divider :key="inote*-1" v-if="inote &gt; 0"></v-divider>
                <v-list-tile :key="note.note_id">
                  <v-list-tile-content>
                    <v-list-tile-title style="white-space: normal">{{ note.note_description }}</v-list-tile-title>
                  </v-list-tile-content>
                </v-list-tile>
              </template>
            </template>
          </v-list>
        </v-card-text>
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
    subjectsList: function () {
      let subjectsList = []

      this.meetData.subjects.each((subject) => {
        let noteTypes = []

        let prvType = null
        let notesList = []
        subject.notes.forEach((item) => {
          if (prvType != null && item.note_type !== prvType) {
            noteTypes.push({
              note_type: prvType,
              notes: notesList
            })
            notesList = []
          }
          notesList.push(item)
          prvType = item.note_type
        })
        if (notesList.length > 0) {
          noteTypes.push({
            note_type: notesList[0].note_type,
            notes: notesList
          })
        }

        if (noteTypes.length > 0) {
          subjectsList.push({
            subject_title: subject.subject_title,
            notes: noteTypes
          })
        }
      })

      return subjectsList
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
  },
  methods: {
    getNoteTitle: function (nodeType) {
      switch (nodeType) {
        case SubjectNoteType.DECISION:
          return this.i18n.title_subject_decisions
        case SubjectNoteType.AGREEMENT:
          return this.i18n.title_subject_agreements
        case SubjectNoteType.UNSETTLED:
          return this.i18n.title_subject_unsettled
        case SubjectNoteType.COMMENT:
          return this.i18n.title_subject_comments
      }
    }
  }
}
</script>

<style scoped>

</style>
