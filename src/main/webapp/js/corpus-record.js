// ///////////////////////////// CorpusRecordCtrl
function CorpusRecordCtrl($scope, $location, $http) {
	$scope.audioContext = new AudioContext();
	$scope.inputPoint = null
	$scope.realAudioInput = null
	$scope.audioInput = null
	$scope.audioRecorder = null;
	$scope.analyserContext = null;
	$scope.rafID = null;
	$scope.state = "down";

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
			
			analyserNode = $scope.audioContext.createAnalyser();
			analyserNode.fftSize = 2048;
			$scope.inputPoint.connect(analyserNode);

			$scope.audioRecorder = new Recorder($scope.inputPoint);

			zeroGain = $scope.audioContext.createGain();
			zeroGain.gain.value = 0.0;
			$scope.inputPoint.connect(zeroGain);
			zeroGain.connect($scope.audioContext.destination);
			$scope.updateAnalysers();

			$scope.state = "waiting";
		});
		console.log('Recorder initialised.' + $scope.state);
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
		$scope.createDownloadLink();

		// $scope.audioRecorder.clear();
		$scope.state = "waiting";
	}

	$scope.createDownloadLink = function() {
		$scope.audioRecorder && $scope.audioRecorder.exportMonoWAV(function(blob) {
			var url = URL.createObjectURL(blob);
			var li = document.createElement('li');
			var au = document.createElement('audio');
			var hf = document.createElement('a');
			var canvas = document.createElement('canvas');

			au.controls = true;
			au.src = url;
			hf.href = url;
			hf.download = new Date().toISOString() + '.wav';
			hf.innerHTML = hf.download;
			canvas.width = 100;
			canvas.height = 100;
			li.appendChild(au);
			li.appendChild(hf);
			recordingslist.appendChild(li);
			$scope.uploadRecord(blob);
			$scope.audioRecorder.getBuffers($scope.drawWave);
		});
	}

	$scope.uploadRecord = function(soundBlob) {
		var fd = new FormData();
		fd.append('file', soundBlob);
		$http.post('/api/corpus/stream', fd, {
			withCredentials : true,
			headers : {
				'Content-Type' : undefined
			},
			transformRequest : angular.identity
		}).success(function(data, status, headers, config) {
			console.log("success data.manualTranscript: " + data.manualTranscript);
		}).error(function(data, status, headers, config) {
			alert(status + ' ' + headers);
		});
	}
	$scope.updateAnalysers = function(time) {
		if (!$scope.analyserContext) {
			var canvas = document.getElementById("analyser");
			canvasWidth = canvas.width;
			canvasHeight = canvas.height;
			$scope.analyserContext = canvas.getContext('2d');
		}

		// analyzer draw code here
		{
			var SPACING = 3;
			var BAR_WIDTH = 1;
			var numBars = Math.round(canvasWidth / SPACING);
			var freqByteData = new Uint8Array(analyserNode.frequencyBinCount);

			analyserNode.getByteFrequencyData(freqByteData);

			$scope.analyserContext.clearRect(0, 0, canvasWidth, canvasHeight);
			$scope.analyserContext.fillStyle = '#F6D565';
			$scope.analyserContext.lineCap = 'round';
			var multiplier = analyserNode.frequencyBinCount / numBars;

			// Draw rectangle for each frequency bin.
			for ( var i = 0; i < numBars; ++i) {
				var magnitude = 0;
				var offset = Math.floor(i * multiplier);
				// gotta sum/average the block, or we miss narrow-bandwidth
				// spikes
				for ( var j = 0; j < multiplier; j++)
					magnitude += freqByteData[offset + j];
				magnitude = magnitude / multiplier;
				var magnitude2 = freqByteData[i * multiplier];
				$scope.analyserContext.fillStyle = "hsl( "
						+ Math.round((i * 360) / numBars) + ", 100%, 50%)";
				$scope.analyserContext.fillRect(i * SPACING, canvasHeight,
						BAR_WIDTH, -magnitude);
			}
		}

		$scope.rafID = window.requestAnimationFrame($scope.updateAnalysers);
	}

	$scope.toggleMono = function() {
		if ($scope.audioInput != $scope.realAudioInput) {
			$scope.audioInput.disconnect();
			$scope.realAudioInput.disconnect();
			$scope.audioInput = $scope.realAudioInput;
		} else {
			$scope.realAudioInput.disconnect();
			$scope.audioInput = $scope.convertToMono($scope.realAudioInput);
		}

		$scope.audioInput.connect($scope.inputPoint);
	}

	$scope.cancelAnalyserUpdates = function() {
		window.cancelAnimationFrame($scope.rafID);
		$scope.rafID = null;
	}
	$scope.convertToMono = function(input) {
		var splitter = $scope.audioContext.createChannelSplitter(2);
		var merger = $scope.audioContext.createChannelMerger(2);

		input.connect(splitter);
		splitter.connect(merger, 0, 0);
		splitter.connect(merger, 0, 1);
		return merger;
	}
	$scope.saveAudio = function() {
		$scope.audioRecorder.exportMonoWAV($scope.doneEncoding);
	}

	$scope.doneEncoding = function(blob) {
		Recorder.forceDownload(blob, "myRecording"
				+ ((recIndex < 10) ? "0" : "") + recIndex + ".wav");
		recIndex++;
	}

	$scope.drawWave = function(buffers) {
		var canvas = document.getElementById("wavedisplay");
		$scope.drawBuffer(canvas.width, canvas.height, canvas.getContext('2d'),
				buffers[0]);
	}

	$scope.drawBuffer = function(width, height, context, data) {
		var step = Math.ceil(data.length / width);
		var amp = height / 2;
		context.fillStyle = "silver";
		for ( var i = 0; i < width; i++) {
			var min = 1.0;
			var max = -1.0;
			for (j = 0; j < step; j++) {
				var datum = data[(i * step) + j];
				if (datum < min)
					min = datum;
				if (datum > max)
					max = datum;
			}
			context.fillRect(i, (1 + min) * amp, 1, Math.max(1, (max - min)
					* amp));
		}
	}

}
