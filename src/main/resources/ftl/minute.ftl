<?xml version="1.0" encoding="UTF-8"?>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<fo:layout-master-set>
		<fo:simple-page-master
			master-name="content"
			page-width="210mm"
			page-height="297mm"
	  	margin-top="10mm"
			margin-bottom="20mm"
			margin-left="25mm"
			margin-right="25mm"
		>
		<fo:region-body margin-top="26mm" margin-bottom="15mm" />
		<fo:region-before extent="26mm"/>
		<fo:region-after extent="15mm"/>
		</fo:simple-page-master>
	</fo:layout-master-set>
	<fo:page-sequence master-reference="content">
	  <fo:static-content flow-name="xsl-region-before">
			<fo:table table-layout="fixed" width="100%" font-size="12pt" color="#5f6368">
				<fo:table-column column-width="proportional-column-width(1)"/>
					<fo:table-body>
						<!-- ============================================================ -->
						<!-- HEADER 																									  	-->
						<!-- ============================================================ -->
						<fo:table-row>
							<fo:table-cell display-align="center">
								<fo:block text-align="center">
									<fo:table table-layout="fixed" width="100%">
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-body>
											<fo:table-row>
												<fo:table-cell display-align="center" number-columns-spanned="2" padding-left='2.0pt' padding-top='2.0pt' padding-bottom='2.0pt' padding-right='2.0pt'>
													<fo:block text-align="center">${i18n.title_meeting}</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row background-color="#336799" color="#FFFFFF">
												<fo:table-cell display-align="left" padding-left='2.0pt' padding-top='2.0pt' padding-bottom='2.0pt' padding-right='2.0pt'>
													<fo:block text-align="left">${i18n.label_reason}: ${meet.getMeetTitle()}</fo:block>
												</fo:table-cell>
												<fo:table-cell display-align="right" padding-left='2.0pt' padding-top='2.0pt' padding-bottom='2.0pt' padding-right='2.0pt'>
													<fo:block text-align="right">${i18n.label_meet_date}: ${meet.getMeetDate()}</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell display-align="right" number-columns-spanned="2" padding-left='2.0pt' padding-top='2.0pt' padding-bottom='2.0pt' padding-right='2.0pt'>
													<fo:block text-align="right">
													${i18n.label_meet_time}: ${meet.getMeetTimeStart()} - ${meet.getMeetTimeEnd()}
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<!-- END - HEADER -->
					</fo:table-body>
			</fo:table>
		</fo:static-content>
	  <fo:static-content flow-name="xsl-region-after">
			<fo:table table-layout="fixed" width="100%" font-size="8pt" color="#5f6368">
				<fo:table-column column-width="proportional-column-width(1)"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell display-align="right" number-columns-spanned="3">
								<fo:block text-align="right">
									<fo:block>${i18n.label_page}: <fo:page-number/> / <fo:page-number-citation ref-id='next-1'/></fo:block>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
			</fo:table>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body">
			<fo:table table-layout="fixed" width="100%"  font-size="12pt" color="#5f6368">
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-body>

					<!-- ============================================================ -->
					<!-- DESCRIPTION 																									-->
					<!-- ============================================================ -->
					<fo:table-row>
						<fo:table-cell display-align="center">
							<fo:block text-align="center">
								<fo:table table-layout="fixed" width="100%" margin-top="5mm" margin-bottom="5mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-body>
											<fo:table-row>
												<fo:table-cell display-align="left" font-size="21pt" font-weight="400" color="#202124">
													<fo:block text-align="left" margin-bottom="10mm">
															${i18n.header_description}:
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell display-align="left">
													<fo:block text-align="justify">
															${meet.getMeetDescription()}
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>


					<!-- END - DESCRIPTION -->

					<!-- ============================================================ -->
					<!-- ATTENDANTS 																									-->
					<!-- ============================================================ -->
					<fo:table-row>
						<fo:table-cell display-align="center">
							<fo:block text-align="center">
								<fo:table table-layout="fixed" width="100%" margin-top="5mm" margin-bottom="5mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell display-align="left" font-size="21pt" font-weight="400" color="#202124">
												<fo:block text-align="left" margin-bottom="10mm">
													${i18n.header_attendants}:
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell display-align="left">
												<fo:block text-align="left">
													<fo:inline font-family="Symbol">&#x2022; </fo:inline>
														${meet.getMeetLeader()}<fo:inline color="#858c93"> - ${i18n.label_leader}</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<#list meet.getMeetAttendants() as attendant>
										<fo:table-row>
											<fo:table-cell display-align="left">
												<fo:block text-align="left">
													<fo:inline font-family="Symbol">&#x2022; </fo:inline>
													${attendant}
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										</#list>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<!-- END ATTENDANTS -->

					<!-- ============================================================ -->
					<!-- SUBJECTS 																										-->
					<!-- ============================================================ -->
					<fo:table-row>
						<fo:table-cell display-align="center">
							<fo:block text-align="center">
								<fo:table table-layout="fixed" width="100%" margin-top="5mm" margin-bottom="5mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell display-align="left" font-size="21pt" font-weight="400" color="#202124">
												<fo:block text-align="left" margin-bottom="10mm">
														${i18n.header_subjects}:
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<#list meet.getMeetSubjectsName() as subject>
										<fo:table-row>
											<fo:table-cell display-align="left">
												<fo:block text-align="left">
													${subject?index + 1}. ${subject}
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										</#list>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<!-- END SUBJECTS -->

					<!-- ============================================================ -->
					<!-- SUBJECT NOTES																								-->
					<!-- ============================================================ -->
					<fo:table-row>
						<fo:table-cell display-align="center">
							<fo:block text-align="center">
								<fo:table table-layout="fixed" width="100%" margin-top="5mm" margin-bottom="5mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell display-align="left" font-size="21pt" font-weight="400" color="#202124">
												<fo:block text-align="left" margin-bottom="10mm">
													${i18n.header_conclusions}:
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<#list meet.getMeetSubjectsWithNotes() as subject>
										<fo:table-row>
											<fo:table-cell display-align="left" font-size="15pt" font-weight="400" color="#202124">
												<fo:block text-align="left" margin-bottom="5mm">
													${subject?index + 1}. ${subject.subject_title}
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
											<#list subject.subject_note_type as note_type>
										<fo:table-row>
											<fo:table-cell display-align="left">
												<fo:block text-align="left">
													<fo:table table-layout="fixed" width="100%" margin-bottom="10mm">
														<fo:table-column column-width="proportional-column-width(1)"/>
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell display-align="left" font-weight="400" color="#202124">
																			<fo:block text-align="left">${i18n['NOTE_' + note_type.note_type]}</fo:block>
																	</fo:table-cell>
																</fo:table-row>
																	<#list note_type.notes as note>
																<fo:table-row>
																	<fo:table-cell display-align="left">
																		<fo:block text-align="justify">
																			<fo:inline font-family="Symbol">&#x2022; </fo:inline> ${note}
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
																	</#list>
															</fo:table-body>
													</fo:table>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
											</#list>
										</#list>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<!-- END SUBJECT NOTES -->
				</fo:table-body>
			</fo:table>
			<fo:block id='next-1'/>
		</fo:flow>
	</fo:page-sequence>
</fo:root>