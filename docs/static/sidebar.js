(function () {
	// 自定义侧边栏标题，添加序号
	function sidebar(hook, vm) {
		hook.doneEach(function () {
			var nav = document.querySelector('.sidebar-nav');
			var nodes = nav.childNodes;

			function visit(ul, prefix) {
				let lis = ul.childNodes;
				for (let i = 0; i < lis.length; i++) {
					let li = lis[i];
					let a = li.querySelector('a');
					if (a){
						a.innerHTML = prefix + (i+1) + ' ' + a.innerHTML;
					}
					let subUl = li.querySelector('ul');
					if (subUl){
						visit(subUl, prefix + (i+1) + '.');
					}
				}
			}

			var layer0 = 0;
			for (var i = 0; i < nodes.length; i++) {
				var node = nodes[i];
				if (node.tagName == 'UL') {
					layer0++;
					visit(node, layer0+'.');
				} else {
					// 考虑过兼容gitbook,可能存在h1/h2等标签，移除其超链接
					var innerHTML = '';
					if (/^H\d+$/.test(node.tagName)) {
						innerHTML = node.querySelector('a').innerHTML;
						if (innerHTML) {
							node.childNodes.forEach((e) => e.remove());
							// node.innerHTML = innerHTML;
							node.outerHTML = innerHTML;
						} else {
							node.remove();
						}
					}
				}
			}
		});
	}

	window.$docsify = window.$docsify || {};
	window.$docsify.plugins = (window.$docsify.plugins || []).concat(sidebar);
})()
