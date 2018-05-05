<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_subjects }} ({{ meetData.subjects.length }})</v-flex>
            <v-spacer></v-spacer>
            <v-flex class="text-xs-right"><v-btn color="primary" @click="createSubject">{{  i18n.btn_addsubject}}</v-btn></v-flex>
          </v-layout>
        </div>
        <div v-if="meetData.subjects.length == 0" v-html="i18n.label_nosubjects"></div>
        <v-list v-else>
          <template v-for="(subject, index) in meetData.subjects">
            <v-divider v-if="index &gt; 0" :key="index" inset></v-divider>
            <v-list-tile :key="subject.subject_id">
              <v-list-tile-action>
                <v-btn flat icon color="primary" @click="deleteSubject(subject)">
                  <v-icon>delete</v-icon>
                </v-btn>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-layout row wrap style="width: 100%" align-center>
                  <v-flex xs8>
                    <v-text-field
                      :name="'subject_title' + index"
                      :placeholder="i18n.label_subject_title"
                      v-model="meetData.subjects[index].subject_title"
                      @input="updateSubject(meetData.subjects[index])"
                    ></v-text-field>
                  </v-flex>
                  <v-flex xs2 pa-0>
                    <v-text-field
                      :name="'subject_duration' + index"
                      :placeholder="i18n.label_subject_duration"
                      v-model="meetData.subjects[index].subject_duration"
                      type="number"
                      suffix="min"
                      step="5"
                      min="0"
                      @input="updateSubject(meetData.subjects[index])"
                    ></v-text-field>
                  </v-flex>
                  <v-flex xs2 pa-0>
                    <v-btn
                      v-if="meetData.subjects[index].subject_priority == SubjectPriority.IRRELEVANT"
                      @click="meetData.subjects[index].subject_priority = SubjectPriority.NORMAL; updateSubject(meetData.subjects[index])"
                      flat
                      icon
                      color="orange">
                      <v-icon>star_border</v-icon>
                    </v-btn>
                    <v-btn
                      v-else-if="meetData.subjects[index].subject_priority == SubjectPriority.NORMAL"
                      @click="meetData.subjects[index].subject_priority = SubjectPriority.ESSENTIAL; updateSubject(meetData.subjects[index])"
                      flat
                      icon
                      color="orange">
                      <v-icon>star_half</v-icon>
                    </v-btn>
                    <v-btn
                      v-else
                      @click="meetData.subjects[index].subject_priority = SubjectPriority.IRRELEVANT; updateSubject(meetData.subjects[index])"
                      flat
                      icon
                      color="orange">
                      <v-icon>star</v-icon>
                    </v-btn>
                  </v-flex>
                </v-layout>
              </v-list-tile-content>
            </v-list-tile>
          </template>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
import { SubjectPriority } from '@/model/subject'

export default {
  name: 'MeetSubjectsEdit',
  props: ['meetData', 'i18nData'],
  data () {
    return {
      SubjectPriority: SubjectPriority,
      updateTimeout: null,
      onUpdate: false,
      i18n: Object.assign({
        title_subjects: 'Subjects',
        label_subject_title: 'Title',
        label_subject_duration: 'Duration',
        label_subject_priority: 'Priority',
        label_nosubjects: 'It looks like you have not defined any subject yet. Add one or more subjects of the meeting.',
        btn_addsubject: 'Add subject',
        btn_delsubject: 'Remove subject'
      }, this.i18nData)
    }
  },
  methods: {
    createSubject: function () {
      this.onUpdate = true
      this.meetData.getRequest({
        url: this.meetData.getURL('/app/meet/{meet_id}/subject/entry', this.meetData),
        method: 'POST'
      }).send().then((response) => {
        this.onUpdate = false
        this.meetData.subjects.fetch().then((response) => {
          console.log('OK')
        })
      }).catch(() => {
        this.onUpdate = false
      })
    },
    deleteSubject: function (subject) {
      subject.delete()
    },
    updateSubject: function (subject) {
      if (this.updateTimeout != null) {
        clearTimeout(this.updateTimeout)
      }

      this.updateTimeout = setTimeout(() => {
        this.updateTimeout = null
        this.onUpdate = true
        subject.save().then(() => {
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
