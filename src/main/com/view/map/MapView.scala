package com.nessie.view.map

trait MapView {
	/** stats the view and interacts with the user */
	def start()

	/** stops the view and cleans all resources used by the view */
	def stop()
}
