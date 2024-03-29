package be.kdg.backend.client.impl;

import be.kdg.backend.client.StreamingClient;
import be.kdg.backend.domain.Recording;
import be.kdg.backend.domain.StudentParticipation;
import be.kdg.backend.dto.restreamer.RestreamerProcessDto;
import be.kdg.backend.dto.restreamer.RestreamerRtmpEntry;
import be.kdg.backend.exception.AuthenticationException;
import be.kdg.backend.util.AuthenticationHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class ReStreamerClientImpl implements StreamingClient {
    //region DI
    private final RestTemplate restTemplate;
    private final AuthenticationHelper authenticationHelper;
    //endregion
    //region CONNECTION ENV VARS
    @Value("${app.stream-connection.http-url}")
    private String STREAMING_SERVER_HTTP_URL;
    @Value("${app.stream-connection.http-url-client}")
    private String STREAMING_SERVER_HTTP_URL_CLIENT;
    @Value("${app.stream-connection.rtmp-url-client}")
    private String STREAMING_SERVER_RTMP_URL_CLIENT;
    @Value("${app.stream-connection.rtmp-token}")
    private String RTMP_TOKEN;
    //endregion
    //region OPTIONS ENV VARS
    @Value("${app.stream-options.thread-queue-size}")
    private int THREAD_QUEUE_SIZE;
    @Value("${app.stream-options.analyze-duration}")
    private int ANALYZE_DURATION;
    @Value("${app.stream-options.hls-time}")
    private int HLS_TIME;
    @Value("${app.stream-options.hls-list-size}")
    private int HLS_LIST_SIZE;
    @Value("${app.stream-options.hls-delete-threshold}")
    private int HLS_DELETE_THRESHOLD;
    @Value("${app.stream-options.master-pl-publish-rate}")
    private int MASTER_PL_PUBLISH_RATE;
    @Value("${app.stream-options.max-file-age-seconds}")
    private int MAX_FILE_AGE_SECONDS;
    @Value("${app.stream-options.reconnect-delay-seconds}")
    private int RECONNECT_DELAY_SECONDS;
    @Value("${app.stream-options.stale-timeout-seconds}")
    private int STALE_TIMEOUT_SECONDS;
    //endregion

    public ReStreamerClientImpl(RestTemplate restTemplate, AuthenticationHelper authenticationHelper) {
        this.restTemplate = restTemplate;
        this.authenticationHelper = authenticationHelper;
    }

    //region INTERFACE METHODS
    @Override
    public List<String> getPublishingStudentIds() {
        var rtmpEntries = getActiveRtmpStreams();
        // We chose to use String.substring() instead of regex or String.split() because it will be faster.
        // As this will be called frequently, we want to keep it as fast as possible.
        // NOTE: If the rtmp application name changes, this will break.
        return rtmpEntries.stream().map(e -> e.getName().substring(11, 21)).toList();
    }

    @Override
    public StudentParticipation createStreamingProcess(StudentParticipation participation) {
        var studentId = participation.getStudentId();
        var participationId = participation.getId();
        var payload = getStreamingProcessPayload(studentId, participationId);

        try {
            createProcess(payload);
            participation.setRtmpStreamUrl(getRtmpStreamUrl(studentId));
            participation.setHlsStreamUrl(getHlsStreamUrl(studentId, participationId));
            return participation;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                participation.setRtmpStreamUrl(getRtmpStreamUrl(studentId));
                participation.setHlsStreamUrl(getHlsStreamUrl(studentId, participationId));
                return participation;
            }
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                authenticationHelper.refreshToken();
                log.error("Error creating streaming process: \n{}", e.getResponseBodyAsString());
                throw new AuthenticationException("Error creating streaming process: \n%s".formatted(e.getResponseBodyAsString()));
            }
            log.error("Error creating streaming process: \n{}", e.getResponseBodyAsString());
            throw new RuntimeException("Error creating streaming process: \n%s".formatted(e.getResponseBodyAsString()));
        } catch (Exception e) {
            log.error("Error creating streaming process: \n{}", e.getMessage());
            throw new RuntimeException("Error creating streaming process: \n%s".formatted(e.getMessage()));
        }
    }

    @Override
    public Recording createRecordingProcess(Recording recording) {
        var studentId = recording.getStudentParticipation().getStudentId();
        var recordingId = recording.getId();
        var payload = getRecordingProcessPayload(studentId, recordingId);

        try {
            createProcess(payload);
            recording.setRecordingUrl(getRecordingUrl(studentId, recordingId));
            return recording;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                recording.setRecordingUrl(getRecordingUrl(studentId, recordingId));
                return recording;
            }
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                authenticationHelper.refreshToken();
                log.error("Error creating recording process: \n{}", e.getResponseBodyAsString());
                throw new AuthenticationException("Error creating recording process: \n%s".formatted(e.getResponseBodyAsString()));
            }
            log.error("Error creating recording process: \n{}", e.getResponseBodyAsString());
            throw new RuntimeException("Error creating recording process: \n%s".formatted(e.getResponseBodyAsString()));
        } catch (Exception e) {
            log.error("Error creating recording process: \n{}", e.getMessage());
            throw new RuntimeException("Error creating recording process: \n%s".formatted(e.getMessage()));
        }
    }

    @Override
    public void stopStreamingProcess(StudentParticipation participation) {
        var processId = getStreamingProcessId(participation.getStudentId(), participation.getId());
        try {
            deleteProcess(processId);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                log.error("Error stopping streaming process: \n{}", e.getResponseBodyAsString());
            }
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                authenticationHelper.refreshToken();
                log.error("Error stopping streaming process: \n{}", e.getResponseBodyAsString());
                throw new AuthenticationException("Error stopping streaming process: \n%s".formatted(e.getResponseBodyAsString()));
            }
        } catch (Exception e) {
            log.error("Error stopping streaming process: \n{}", e.getMessage());
            throw new RuntimeException("Error stopping streaming process: \n%s".formatted(e.getMessage()));
        }
    }

    @Override
    public void stopRecordingProcess(Recording recording) {
        var processId = getRecordingProcessId(recording.getStudentParticipation().getStudentId(), recording.getId());
        try {
            deleteProcess(processId);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                log.error("Error stopping recording process: \n{}", e.getResponseBodyAsString());
            }
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                authenticationHelper.refreshToken();
                log.error("Error stopping recording process: \n{}", e.getResponseBodyAsString());
                throw new AuthenticationException("Error stopping recording process: \n%s".formatted(e.getResponseBodyAsString()));
            }
        } catch (Exception e) {
            log.error("Error stopping recording process: \n{}", e.getMessage());
            throw new RuntimeException("Error stopping recording process: \n%s".formatted(e.getMessage()));
        }
    }

    @Override
    public void stopAllProcesses() {
        try {
            var processes = getAllProcesses();
            for (var process : processes) {
                deleteProcess(process.getId());
            }
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                authenticationHelper.refreshToken();
                log.error("Error stopping all processes: \n{}", e.getResponseBodyAsString());
                throw new AuthenticationException("Error stopping all processes: \n%s".formatted(e.getResponseBodyAsString()));
            }
        }
    }
    //endregion

    //region API CALLS
    private void createProcess(String payload) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationHelper.getAccessToken());
        var request = new HttpEntity<>(payload, headers);

        restTemplate.exchange(STREAMING_SERVER_HTTP_URL + "/api/v3/process", HttpMethod.POST, request, String.class);
    }

    private void deleteProcess(String processId) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(authenticationHelper.getAccessToken());
        var request = new HttpEntity<>(headers);
        restTemplate.exchange(STREAMING_SERVER_HTTP_URL + "/api/v3/process/" + processId, HttpMethod.DELETE, request, String.class);
    }

    private List<RestreamerProcessDto> getAllProcesses() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(authenticationHelper.getAccessToken());
        var request = new HttpEntity<>(null, headers);

        var response = restTemplate.exchange(STREAMING_SERVER_HTTP_URL + "/api/v3/process", HttpMethod.GET, request, RestreamerProcessDto[].class);
        var responseBody = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || responseBody == null) {
            throw new RuntimeException("Error getting all processes %s: \n%s".formatted(response.getStatusCode().toString(), response.getBody()));
        }

        return List.of(responseBody);
    }

    private List<RestreamerRtmpEntry> getActiveRtmpStreams() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(authenticationHelper.getAccessToken());
        var request = new HttpEntity<>(null, headers);

        var response = restTemplate.exchange(STREAMING_SERVER_HTTP_URL + "/api/v3/rtmp", HttpMethod.GET, request, RestreamerRtmpEntry[].class);
        var responseBody = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || responseBody == null) {
            throw new RuntimeException("Error getting active rtmp streams %s: \n%s".formatted(response.getStatusCode().toString(), response.getBody()));
        }

        return List.of(responseBody);
    }
    //endregion

    //region HELPER METHODS
    private String getStreamingProcessPayload(String studentId, Long participationId) {
        var processId = getStreamingProcessId(studentId, participationId);
        var fileName = getFileName(studentId, participationId);
        var payload = """
                {
                	"type": "ffmpeg",
                	"id": "${processId}",
                	"reference": "${fileName}",
                	"input": [
                		{
                			"id": "input_0",
                			"address": "{rtmp,name=${studentId}.stream}",
                			"options": [
                				"-fflags",
                				"+genpts",
                				"-thread_queue_size",
                				"${threadQueueSize}",
                				"-analyzeduration",
                				"${analyzeDuration}"
                			]
                		}
                	],
                	"output": [
                		{
                			"id": "output_0",
                			"address": "{memfs}/${fileName}_{outputid}.m3u8",
                			"options": [
                				"-dn",
                				"-sn",
                				"-map",
                				"0:0",
                				"-codec:v",
                				"copy",
                				"-an",
                				"-f",
                				"hls",
                				"-start_number",
                				"0",
                				"-hls_time",
                				"${hlsTime}",
                				"-hls_list_size",
                				"${hlsListSize}",
                				"-hls_flags",
                				"append_list+delete_segments+program_date_time+temp_file",
                				"-hls_delete_threshold",
                				"${hlsDeleteThreshold}",
                				"-hls_segment_filename",
                				"{memfs}/${fileName}_{outputid}_%04d.ts",
                				"-master_pl_name",
                				"${fileName}.m3u8",
                				"-master_pl_publish_rate",
                				"${masterPlPublishRate}",
                				"-method",
                				"PUT"
                			],
                			"cleanup": [
                				{
                					"pattern": "memfs:/${fileName}**",
                					"max_files": 0,
                					"max_file_age_seconds": 0,
                					"purge_on_delete": true
                				},
                				{
                					"pattern": "memfs:/${fileName}_{outputid}.m3u8",
                					"max_files": 0,
                					"max_file_age_seconds": ${maxFileAgeSeconds},
                					"purge_on_delete": true
                				},
                				{
                					"pattern": "memfs:/${fileName}_{outputid}_**.ts",
                					"max_files": 12,
                					"max_file_age_seconds": ${maxFileAgeSeconds},
                					"purge_on_delete": true
                				},
                				{
                					"pattern": "memfs:/${fileName}.m3u8",
                					"max_files": 0,
                					"max_file_age_seconds": ${maxFileAgeSeconds},
                					"purge_on_delete": true
                				}
                			]
                		}
                	],
                	"options": [
                		"-err_detect",
                		"ignore_err",
                		"-y"
                	],
                	"reconnect": true,
                	"reconnect_delay_seconds": ${reconnectDelaySeconds},
                	"autostart": true,
                	"stale_timeout_seconds": ${staleTimeOutSeconds},
                	"limits": {
                		"cpu_usage": 0,
                		"memory_mbytes": 0,
                		"waitfor_seconds": 5
                	}
                }
                """;
        var valueMap = new HashMap<String, Object>();
        populateBasicValueMap(valueMap, studentId, processId, fileName);
        populateHlsValueMap(valueMap);
        var sub = new StringSubstitutor(valueMap);
        return sub.replace(payload);
    }

    private String getRecordingProcessPayload(String studentId, Long recordingId) {
        var processId = getRecordingProcessId(studentId, recordingId);
        var fileName = getFileName(studentId, recordingId);
        var payload = """
                {
                    "type":"ffmpeg",
                    "id":"${processId}",
                    "reference":"${processId}",
                    "input":[
                        {
                            "id":"input_0",
                            "address":"{rtmp,name=${studentId}.stream}",
                            "options":[
                                "-fflags",
                                "+genpts",
                                "-thread_queue_size",
                                "${threadQueueSize}",
                                "-analyzeduration",
                                "${analyzeDuration}"
                            ]
                        }
                    ],
                    "output":[
                        {
                            "id":"output_0",
                            "address":"{diskfs}/recordings/${fileName}.mp4",
                            "options":[]
                        }
                    ],
                    "options":[
                        "-err_detect",
                        "ignore_err",
                        "-y"
                    ],
                    "autostart":true,
                    "reconnect":true,
                    "reconnect_delay_seconds":${reconnectDelaySeconds},
                    "stale_timeout_seconds":${staleTimeOutSeconds},
                    "limits":{
                        "cpu_usage":0,
                        "memory_mbytes":0,
                        "waitfor_seconds":5
                    }
                }
                """;
        var valueMap = new HashMap<String, Object>();
        populateBasicValueMap(valueMap, studentId, processId, fileName);
        var sub = new StringSubstitutor(valueMap);
        return sub.replace(payload);
    }

    private void populateBasicValueMap(HashMap<String, Object> valueMap, String studentId, String processId, String fileName) {
        valueMap.put("studentId", studentId);
        valueMap.put("processId", processId);
        valueMap.put("fileName", fileName);
        valueMap.put("threadQueueSize", THREAD_QUEUE_SIZE);
        valueMap.put("analyzeDuration", ANALYZE_DURATION);
        valueMap.put("reconnectDelaySeconds", RECONNECT_DELAY_SECONDS);
        valueMap.put("staleTimeOutSeconds", STALE_TIMEOUT_SECONDS);
    }

    private void populateHlsValueMap(HashMap<String, Object> valueMap) {
        valueMap.put("hlsTime", HLS_TIME);
        valueMap.put("hlsListSize", HLS_LIST_SIZE);
        valueMap.put("hlsDeleteThreshold", HLS_DELETE_THRESHOLD);
        valueMap.put("masterPlPublishRate", MASTER_PL_PUBLISH_RATE);
        valueMap.put("maxFileAgeSeconds", MAX_FILE_AGE_SECONDS);
    }

    private String getFileName(String studentId, Long participationId) {
        return "%s_%d".formatted(studentId, participationId);
    }

    private String getStreamingProcessId(String studentId, Long participationId) {
        return "exam-tool:ingest:%s".formatted(getFileName(studentId, participationId));
    }

    private String getRecordingProcessId(String studentId, Long recordingId) {
        return "exam-tool:record:%s".formatted(getFileName(studentId, recordingId));
    }

    private String getRtmpStreamUrl(String studentId) {
        return "%s/%s.stream?token=%s".formatted(STREAMING_SERVER_RTMP_URL_CLIENT, studentId, RTMP_TOKEN);
    }

    private String getHlsStreamUrl(String studentId, Long participationId) {
        return "%s/memfs/%s.m3u8".formatted(STREAMING_SERVER_HTTP_URL_CLIENT, getFileName(studentId, participationId));
    }

    private String getRecordingUrl(String studentId, Long recordingId) {
        return "%s/recordings/%s.mp4".formatted(STREAMING_SERVER_HTTP_URL_CLIENT, getFileName(studentId, recordingId));
    }
    //endregion
}
