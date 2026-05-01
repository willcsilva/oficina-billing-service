{{/*
Define o nome do service account a ser usado
*/}}
{{- define "billing.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "billing.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}