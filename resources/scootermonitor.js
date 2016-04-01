'use strict';
/**
 *
 */

(function() {


var appCommand = angular.module('scooterapp', ['googlechart', 'ui.bootstrap']);






// --------------------------------------------------------------------------
//
// Controler Ping
//
// --------------------------------------------------------------------------

// Ping the server
appCommand.controller('ScooterController',
	function ( $http, $scope ) {

	this.urlmobile ="";
	
	this.getinfo = function() {
		
		var self=this;
		$http.get( '?page=custompage_scooter&action=geturlmobile' )
				.success( function ( jsonResult ) {
						self.listaddress 		= jsonResult.listaddress;
						self.allipadress 	= jsonResult.allipadress;
						
				})
				.error( function() {
					alert('an error occure');
					});
				
	};
	this.getinfo();
	
	this.getqrcode = function() {
		alert('getQrCode');
		var self=this;
		$http.get( '?page=custompage_scooter&action=getqrcode' )
				.then( 
					function ( jsonResult ) {
					alert('getAnswer '+jsonResult.data );
					
				},
				function( response ) {
					alert('an error occure '+response.status);
					});
				
	};
	// this.getqrcode();

});



})();