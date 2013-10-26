// ///////////////////////////// CorpusRecognitionCtrl
function CorpusRecognitionCtrl($scope, $location, $http) {
	$scope.audioContext = new AudioContext();
	$scope.inputPoint = null
	$scope.realAudioInput = null
	$scope.audioInput = null
	$scope.audioRecorder = null;
	$scope.analyserContext = null;
	$scope.state = "down";
	$scope.recognitionResult = [];
	$scope.previousPhrase = null;
	$scope.phrase = null;
	
	$scope.message = "";


	
	$scope.fetchNextPhrase = function(){
		$http.get('/api/recognize/phrase').success(function(data) {
			$scope.previousPhrase = $scope.phrase; 
			$scope.phrase = data;
			console.log('Phrase: ' + $scope.phrase );
		});
	} 



	$scope.initRecording = function() {
		if (!navigator.getUserMedia)
			navigator.getUserMedia = navigator.webkitGetUserMedia
					|| navigator.mozGetUserMedia;
		if (!navigator.cancelAnimationFrame)
			navigator.cancelAnimationFrame = navigator.webkitCancelAnimationFrame
					|| navigator.mozCancelAnimationFrame;
		if (!navigator.requestAnimationFrame)
			navigator.requestAnimationFrame = navigator.webkitRequestAnimationFrame
					|| navigator.mozRequestAnimationFrame;

		navigator.getUserMedia({
			audio : true
		}, startUserMedia, function(e) {
			alert('Error getting audio');
			console.log(e);
		});
	}
	/**
	 * start audio automatically
	 */ 
	$scope.initRecording();
	/**
	 * Callback from initRecording()
	 */
	function startUserMedia(stream) {
		console.log('Input connected to audio context destination.');
		$scope.$apply(function() {
			// var input = $scope.audio_context.createMediaStreamSource(stream);
			$scope.inputPoint = $scope.audioContext.createGain();
			$scope.realAudioInput = $scope.audioContext
					.createMediaStreamSource(stream);
			$scope.audioInput = $scope.realAudioInput;
			$scope.audioInput.connect($scope.inputPoint);

			$scope.audioRecorder = new Recorder($scope.inputPoint);

			zeroGain = $scope.audioContext.createGain();
			zeroGain.gain.value = 0.0;
			$scope.inputPoint.connect(zeroGain);
			zeroGain.connect($scope.audioContext.destination);

			$scope.state = "waiting";
			$scope.message = "Ready. Click record to start recognition.";
		});
		console.log('Recorder initialised.' + $scope.state);
		$scope.fetchNextPhrase();
	}

	$scope.startRecording = function() {
		$scope.state = "recording";
		if (!$scope.audioRecorder) {
			return;
		}
		$scope.audioRecorder.clear();
		$scope.audioRecorder.record();
		console.log('Recording...' + $scope.state);

	}

	$scope.stopRecording = function() {
		console.log('Stopped recording.' + $scope.audioRecorder);
		$scope.audioRecorder.stop();

		// create WAV download link using audio data blob
		$scope.audioRecorder && $scope.audioRecorder.exportMonoWAV(function(blob) {
			$scope.uploadRecord(blob);
		});

		// $scope.audioRecorder.clear();
		$scope.state = "waiting";
	}

	$scope.uploadRecord = function(soundBlob) {
		$scope.message = "requesting server to process";
		var fd = new FormData();
		fd.append('file', soundBlob);
		var gramma = $scope.phrase.grammar;
		var transcribe = $scope.phrase.transcribe;
		$http.post('/api/recognize/stream/'+gramma+ '/'+transcribe , fd, {
			withCredentials : true,
			headers : {
				'Content-Type' : undefined
			},
			transformRequest : angular.identity
		}).success(function(data, status, headers, config) {
			console.log("success data.manualTranscript: " + data.result);
			//$.each( data.resultItems, function( i, resultItem ){
			//	$scope.recognitionResult.unshift(resultItem);	
			//});
			$scope.recognitionResult.unshift({phrase:$scope.phrase.transcribe, recognition:data});
			$scope.message = "Ready. Click record to start recognition.";
			console.log("Retrieved: "+data.resultItems.length);
			$scope.fetchNextPhrase();
		}).error(function(data, status, headers, config) {
			alert(status + ' ' + headers);
		});
	}

}
